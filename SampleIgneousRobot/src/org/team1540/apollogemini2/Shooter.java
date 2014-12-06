package org.team1540.apollogemini2;

import ccre.channel.BooleanInput;
import ccre.channel.BooleanInputPoll;
import ccre.channel.BooleanOutput;
import ccre.channel.BooleanStatus;
import ccre.channel.EventInput;
import ccre.channel.EventOutput;
import ccre.channel.FloatInput;
import ccre.channel.FloatInputPoll;
import ccre.channel.FloatOutput;
import ccre.channel.FloatStatus;
import ccre.cluck.Cluck;
import ccre.ctrl.BooleanMixing;
import ccre.ctrl.EventMixing;
import ccre.ctrl.ExpirationTimer;
import ccre.ctrl.FloatMixing;
import ccre.ctrl.Mixing;
import ccre.holders.TuningContext;
import ccre.igneous.Igneous;
import ccre.log.Logger;

public class Shooter {
    private static final TuningContext shooterTuningContext = new TuningContext("ShooterValues").publishSavingEvent();

    public static final BooleanStatus winchDisengaged = new BooleanStatus();
    public static final BooleanStatus rearming = new BooleanStatus();

    private static final FloatStatus wattageAccumulator = new FloatStatus();

    private static final FloatInput drawBack = shooterTuningContext.getFloat("Draw Back", 650);
    private static final FloatInput winchSpeedSetting = shooterTuningContext.getFloat("Winch Speed", 1f);
    private static final FloatInput rearmTimeout = shooterTuningContext.getFloat("Winch Rearm Timeout", 5f);

    private static final BooleanInputPoll isArmInTheWay = BooleanMixing.invert(Actuators.isSafeToShoot);
    private static final BooleanInput winchPastThreshold = FloatMixing.floatIsAtLeast(wattageAccumulator, drawBack);

    private static final FloatOutput winchMotor;
    private static final FloatInputPoll winchCurrent;
    private static final BooleanOutput winchSolenoidDisengage;

    static {
        if (Igneous.isRoboRIO()) {
            winchMotor = Igneous.makeTalonMotor(6, Igneous.MOTOR_REVERSE, Igneous.NO_RAMPING);
            winchSolenoidDisengage = Igneous.makeSolenoid(4);
            winchCurrent = Igneous.getPDPChannelCurrent(12);
        } else {
            winchMotor = Igneous.makeVictorMotor(5, Igneous.MOTOR_REVERSE, Igneous.NO_RAMPING);
            winchSolenoidDisengage = Igneous.makeSolenoid(3);
            winchCurrent = Igneous.makeAnalogInput(1);
        }
    }

    private static final FloatInput activeAmps = FloatMixing.createDispatch(new FloatInputPoll() {
        private final float tare = winchCurrent.get();

        private final FloatInput ampThreshold = shooterTuningContext.getFloat("Amp Threshold", 5f);

        public float get() {
            float o;
            if (Igneous.isRoboRIO()) {
                o = winchCurrent.get() - tare;
            } else {
                o = (winchCurrent.get() - 0.60f) / 0.04f;
            }
            return o >= ampThreshold.get() ? o : 0;
        }
    }, Igneous.constantPeriodic);

    private static final FloatInput activeWatts = FloatMixing.multiplication.of(activeAmps, Igneous.getBatteryVoltage());

    static {
        // Update wattage total
        activeWatts.send(new FloatOutput() {
            private long lastReadingAt = System.currentTimeMillis();

            public void set(float value) {
                long now = System.currentTimeMillis();
                wattageAccumulator.set(wattageAccumulator.get() + activeWatts.get() * (now - lastReadingAt) / 1000f);
                lastReadingAt = now;
            }
        });
    }

    // shouldUseCurrent = false

    public static void setup() {
        Cluck.publish("testing-winch-motor", winchMotor);
        Cluck.publish("testing-solenoid-winch", winchSolenoidDisengage);
        Cluck.publish("Winch Current", FloatMixing.createDispatch(winchCurrent, Igneous.globalPeriodic));
        EventInput fireWhen = EventMixing.combine(AutonomousFramework.getWhenToFire(), UserInterface.getFireButton());

        winchDisengaged.setFalseWhen(Igneous.startDisabled);
        rearming.setFalseWhen(Igneous.startDisabled);

        winchDisengaged.send(winchSolenoidDisengage);
        Cluck.publish("Winch Disengaged", winchDisengaged);

        rearming.send(Mixing.select(winchMotor, FloatMixing.always(0), winchSpeedSetting));

        Cluck.publish("ActiveAmps", activeAmps);
        Cluck.publish("ActiveWatts", activeWatts);
        Cluck.publish("TotalWatts", wattageAccumulator);

        EventInput rearmEvent = UserInterface.getRearmCatapult();

        final FloatStatus resetRearm = new FloatStatus();
        resetRearm.setWhen(0, BooleanMixing.whenBooleanBecomes(rearming, false));
        Cluck.publish("Winch Rearm Timeout Status", (FloatInput) resetRearm);
        Igneous.constantPeriodic.send(new EventOutput() {
            public void event() {
                float val = resetRearm.get();
                if (val > 0) {
                    val -= 0.01f;
                    if (val <= 0 && rearming.get()) {
                        Logger.info("Rearm Timeout");
                        rearming.set(false);
                    }
                    resetRearm.set(val);
                } else if (rearming.get()) {
                    resetRearm.set(rearmTimeout.get());
                }
            }
        });

        final EventOutput realFire = new EventOutput() {
            public void event() {
                ReadoutDisplay.displayAndLogError(1, "Firing", 500);
                winchDisengaged.set(true);
            }
        };
        Cluck.publish("Force Fire", realFire);
        final ExpirationTimer fireAfterLower = new ExpirationTimer();

        EventOutput guardedFire = new EventOutput() {
            public void event() {
                if (rearming.get()) {
                    rearming.set(false);
                    ReadoutDisplay.displayAndLogError(5, "Cancelled rearm.", 1000);
                } else if (winchDisengaged.get()) {
                    ReadoutDisplay.displayAndLogError(3, "Winch not armed.", 2000);
                } else if (isArmInTheWay.get()) {
                    ReadoutDisplay.displayAndLogError(4, "Autolowering arm.", 1000);
                    fireAfterLower.startOrFeed();
                } else {
                    realFire.event();
                }
            }
        };

        fireAfterLower.schedule(50, Actuators.armLowerForShooter);
        fireAfterLower.schedule(1200, guardedFire);
        fireAfterLower.schedule(1300, fireAfterLower.getStopEvent());

        fireWhen.send(guardedFire);

        EventMixing.combine(AutonomousFramework.getWhenToRearm(), rearmEvent).send(new EventOutput() {
            public void event() {
                if (rearming.get()) {
                    rearming.set(false);
                    ReadoutDisplay.displayAndLogError(5, "Cancelled rearm.", 1000);
                } else if (isArmInTheWay.get()) {
                    ReadoutDisplay.displayAndLogError(4, "Arm isn't down.", 500);
                } else {
                    winchDisengaged.set(false);
                    ReadoutDisplay.displayAndLogError(1, "Started rearming.", 500);
                    rearming.set(true);
                    wattageAccumulator.set(0);
                }
            }
        });
        Igneous.globalPeriodic.send(new EventOutput() {
            public void event() {
                if (rearming.get() && winchPastThreshold.get()) {
                    rearming.set(false);
                    ReadoutDisplay.displayAndLogError(2, "Hit current limit.", 1000);
                    AutonomousFramework.notifyRearmFinished();
                }
            }
        });

        ReadoutDisplay.showWinchStatus(wattageAccumulator);
        UserInterface.showFiring(winchDisengaged);
    }

    public static BooleanInput getShouldDisableDrivingAndCompressor() {
        return rearming;
    }
}