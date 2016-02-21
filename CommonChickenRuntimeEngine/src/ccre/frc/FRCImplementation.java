/*
 * Copyright 2013-2016 Cel Skeggs
 * Copyright 2015 Jake Springer
 *
 * This file is part of the CCRE, the Common Chicken Runtime Engine.
 *
 * The CCRE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * The CCRE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the CCRE.  If not, see <http://www.gnu.org/licenses/>.
 */
package ccre.frc;

import ccre.bus.I2CBus;
import ccre.bus.RS232Bus;
import ccre.bus.SPIBus;
import ccre.channel.BooleanInput;
import ccre.channel.BooleanOutput;
import ccre.channel.EventInput;
import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.ctrl.ExtendedMotor;
import ccre.ctrl.Joystick;
import ccre.ctrl.binding.ControlBindingCreator;
import ccre.discrete.DiscreteInput;
import ccre.drivers.ctre.talon.TalonExtendedMotor;

/**
 * This is an implementation for an FRC application. The reason for this is so
 * that the main program can be ran without a robot. Documentation for all the
 * methods here can be found in FRC, where they are invoked.
 *
 * @author skeggsc
 */
public interface FRCImplementation {

    /**
     * The ID for a Jaguar speed controller.
     */
    public static final int JAGUAR = 1;
    /**
     * The ID for a Talon SR speed controller.
     */
    public static final int TALON = 2;
    /**
     * The ID for a Victor speed controller.
     */
    public static final int VICTOR = 3;
    /**
     * The ID for a Victor SP speed controller.
     */
    public static final int VICTORSP = 4;
    /**
     * The ID for a Spark speed controller.
     */
    public static final int SPARK = 5;
    /**
     * The ID for a SD540 speed controller.
     */
    public static final int SD540 = 6;
    /**
     * The ID for a Talon SRX speed controller.
     */
    public static final int TALONSRX = 7;

    /**
     * Accesses a Joystick from the Driver Station.
     *
     * @param id the Joystick number (1-4)
     * @return the associated joystick on the driver station.
     */
    public Joystick getJoystick(int id);

    /**
     * Create a reference to a speed controller.
     *
     * @param id the motor port ID, from 1 to 10, inclusive.
     * @param type the motor type: JAGUAR, VICTOR, or TALON.
     * @return the output that will output to the specified motor.
     * @see #JAGUAR
     * @see #VICTOR
     * @see #TALON
     */
    public FloatOutput makeMotor(int id, int type);

    /**
     * Create a reference to a CAN Jaguar.
     *
     * @param deviceNumber the Jaguar's CAN device number.
     * @return the ExtendedMotor for the Jaguar.
     */
    public ExtendedMotor makeCANJaguar(int deviceNumber);

    /**
     * Create a reference to a CAN Talon.
     *
     * @param deviceNumber the Talon's CAN device number.
     * @return the ExtendedMotor for the Talon.
     */
    public TalonExtendedMotor makeCANTalon(int deviceNumber);

    /**
     * Create a reference to a solenoid on the specified port and module.
     *
     * Module #0 should be interpreted as the default module.
     *
     * @param module the solenoid module (PCM on roboRIO)
     * @param id the port of the solenoid.
     * @return the output that will control the solenoid.
     */
    public BooleanOutput makeSolenoid(int module, int id);

    /**
     * Create a reference to a digital output on the specified port.
     *
     * @param id the port of the digital output.
     * @return the output that will control the digital output.
     */
    public BooleanOutput makeDigitalOutput(int id);

    /**
     * Create a reference to an analog input on the specified port with the
     * default number of average bits.
     *
     * @param id the port number.
     * @param updateOn when the analog input should be polled.
     * @return the analog input, reporting in voltage.
     */
    public FloatInput makeAnalogInput(int id, EventInput updateOn);

    /**
     * Create a reference to an analog input on the specified port with the
     * specified number of average bits.
     *
     * @param id the port number.
     * @param averageBits the number of averaging bits.
     * @param updateOn when the analog input should be polled.
     * @return the analog input, reporting in voltage.
     */
    public FloatInput makeAnalogInput(int id, int averageBits, EventInput updateOn);

    /**
     * Create a reference to a digital input on the specified port.
     *
     * @param id the port number.
     * @param updateOn when the digital input should be polled.
     * @return the digital input.
     */
    public BooleanInput makeDigitalInput(int id, EventInput updateOn);

    /**
     * Create a reference to a digital input on the specified port, as a
     * BooleanInput that updates by using FPGA interrupts or an equivalent.
     * Warning: many systems have a limit on the maximum number of interrupts
     * available - use sparingly!
     *
     * @param id the port number.
     * @return the digital input.
     */
    public BooleanInput makeDigitalInputByInterrupt(int id);

    /**
     * Create a reference to a servo controller for the specified port and
     * minimum and maximum values.
     *
     * @param id the port number.
     * @param minInput the value on the output that should correspond to the
     * servo's minimum position.
     * @param maxInput the value on the output that should correspond to the
     * servo's maximum position.
     * @return the FloatOutput that controls the servo.
     */
    public FloatOutput makeServo(int id, float minInput, float maxInput);

    /**
     * Get a boolean input that checks if the robot is currently disabled.
     *
     * @return the input.
     */
    public BooleanInput getIsDisabled();

    /**
     * Get a boolean input that checks if the robot is currently enabled in
     * autonomous.
     *
     * @return the input.
     */
    public BooleanInput getIsAutonomous();

    /**
     * Get a boolean input that checks if the robot is currently enabled in
     * testing mode.
     *
     * @return the input.
     */
    public BooleanInput getIsTest();

    public DiscreteInput<FRCMode> getMode();

    /**
     * Get a boolean input that checks if the robot is currently connected to
     * the FMS, as opposed to being off of the playing field.
     *
     * @return the input.
     */
    public BooleanInput getIsFMS();

    /**
     * Create a reference to an Encoder on the specified ports with the
     * specified number of average bits.
     *
     * @param aChannel The alpha-channel for the encoder.
     * @param bChannel The beta-channel for the encoder.
     * @param reverse Should the result of the encoder be negated?
     * @param resetWhen If provided, the Encoder's value will be reset when this
     * event is produced.
     * @param updateOn when the encoder should be polled.
     * @return a FloatInput that represents the current encoder value
     */
    public FloatInput makeEncoder(int aChannel, int bChannel, boolean reverse, EventInput resetWhen, EventInput updateOn);

    /**
     * Creates a reference to a Counter on the specified port. When the channel
     * is pulsed the counter will count up.
     *
     * @param upChannel The DIO channel that will count up
     * @param downChannel The DIO channel that will count down
     * @param resetWhen If provided, the Counter's value will be reset when this
     * event is produced.
     * @param updateOn when the counter should be polled.
     * @param mode the pulse counting mode for the counter.
     * @return a FloatInput that represents the current counter value
     */
    public FloatInput makeCounter(int upChannel, int downChannel, EventInput resetWhen, EventInput updateOn, int mode);

    /**
     * Create a reference to the Forward side of the relay on the specified
     * channel - this side can be turned on and off.
     *
     * @param channel The relay channel.
     * @return the output that will modify the forward side of the channel.
     */
    public BooleanOutput makeRelayForwardOutput(int channel);

    /**
     * Create a reference to the Reverse side of the relay on the specified
     * channel - this side can be turned on and off.
     *
     * @param channel The relay channel.
     * @return the output that will modify the reverse side of the channel.
     */
    public BooleanOutput makeRelayReverseOutput(int channel);

    /**
     * Create a reference to a Gyro on the specified port with the specified
     * sensitivity. This will allow reading the current rotation of the Gyro.
     * This also takes an EventInput, and when this is fired, the Gyro will be
     * reset.
     *
     * Increased sensitivity means a smaller output for the same turn.
     *
     * @param port The Gyro port number.
     * @param sensitivity The sensitivity of the Gyro. This is the number of
     * volts/degree/second sensitivity of the gyro and is used in calculations
     * to allow the code to work with multiple gyros. 0.007 is a good default
     * value.
     * @param resetWhen When to reset the Gyro.
     * @param updateOn when the gyro should be polled.
     * @return The reference to the Gyro's current value.
     */
    public FloatInput makeGyro(int port, double sensitivity, EventInput resetWhen, EventInput updateOn);

    /**
     * Get a reference to the analog input that reads the current battery
     * voltage, scaled to represent the real battery voltage.
     *
     * @param updateOn when the battery should be polled.
     * @return The current battery voltage.
     */
    public FloatInput getBatteryVoltage(EventInput updateOn);

    /**
     * @return an event that is produced during every mode if the driver station
     * is attached.
     */
    public EventInput getGlobalPeriodic();

    /**
     * @return an event that is produced when the robot enters autonomous mode.
     */
    public EventInput getStartAuto();

    /**
     * @return an event that is produced during autonomous mode.
     */
    public EventInput getDuringAuto();

    /**
     * @return an event that is produced when the robot enters teleop mode.
     */
    public EventInput getStartTele();

    /**
     * @return an event that is produced during teleop mode.
     */
    public EventInput getDuringTele();

    /**
     * @return an event that is produced when the robot enters testing mode.
     */
    public EventInput getStartTest();

    /**
     * @return an event that is produced during testing mode.
     */
    public EventInput getDuringTest();

    /**
     * @return an event that is produced when the robot enters disabled mode.
     */
    public EventInput getStartDisabled();

    /**
     * @return an event that is produced while the robot is disabled.
     */
    public EventInput getDuringDisabled();

    /**
     * @return an output that enables and disabled the PCM compressor's
     * closed-loop control.
     */
    public BooleanOutput usePCMCompressor();

    /**
     * @param updateOn when the PCM should be polled.
     * @return the status of the PCM pressure switch.
     */
    public BooleanInput getPCMPressureSwitch(EventInput updateOn);

    /**
     * @param updateOn when the PCM should be polled.
     * @return the status of the PCM compressor enable.
     */
    public BooleanInput getPCMCompressorRunning(EventInput updateOn);

    /**
     * @param updateOn when the PCM should be polled.
     * @return the current draw of the PCM compressor.
     */
    public FloatInput getPCMCompressorCurrent(EventInput updateOn);

    /**
     * @param updateOn when the PDP should be polled.
     * @return the current draw of the entire PDP.
     */
    public FloatInput getPDPTotalCurrent(EventInput updateOn);

    /**
     * @param channel the channel to monitor.
     * @param updateOn when the PDP should be polled.
     * @return the current draw of the specified PDP channel.
     */
    public FloatInput getPDPChannelCurrent(int channel, EventInput updateOn);

    /**
     * @param updateOn when the PDP should be polled.
     * @return the voltage measured at the PDP.
     */
    public FloatInput getPDPVoltage(EventInput updateOn);

    /**
     * @param deviceName the name of the device the serial port is connected to
     * (used for debugging and the emulator.)
     * @return a SerialIO interface to the port.
     */
    public RS232Bus makeRS232_Onboard(String deviceName);

    /**
     * @param deviceName the name of the device the serial port is connected to
     * (used for debugging and the emulator.)
     * @return a SerialIO interface to the port.
     */
    public RS232Bus makeRS232_MXP(String deviceName);

    /**
     * @param deviceName the name of the device the serial port is connected to
     * (used for debugging and the emulator.)
     * @return a SerialIO interface to the port.
     */
    public RS232Bus makeRS232_USB(String deviceName);

    /**
     * @param powerChannel the power channel to monitor.
     * @param updateOn when the channel should be polled.
     * @return the active voltage of the channel.
     */
    public FloatInput getChannelVoltage(int powerChannel, EventInput updateOn);

    /**
     * @param powerChannel the power channel to monitor.
     * @param updateOn when the channel should be polled.
     * @return the active current of the channel.
     */
    public FloatInput getChannelCurrent(int powerChannel, EventInput updateOn);

    /**
     * @param powerChannel the power channel to monitor.
     * @param updateOn when the channel should be polled.
     * @return whether or not the channel is enabled.
     */
    public BooleanInput getChannelEnabled(int powerChannel, EventInput updateOn);

    /**
     * @param name the name of this control binding client.
     * @return a ControlBindingCreator if there is special emulation available,
     * but otherwise just return null.
     */
    public ControlBindingCreator tryMakeControlBindingCreator(String name);

    /**
     * @return an event that is fired once, right after the user program has
     * finished its initialization
     */
    public EventInput getOnInitComplete();

    public I2CBus makeI2C_Onboard(String deviceName);

    public I2CBus makeI2C_MXP(String deviceName);

    public SPIBus makeSPI_Onboard(int cs, String deviceName);

    public SPIBus makeSPI_MXP(String deviceName);
}
