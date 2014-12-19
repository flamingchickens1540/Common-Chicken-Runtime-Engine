package ccre.igneous;

import ccre.channel.BooleanOutput;
import ccre.channel.FloatInputPoll;
import ccre.channel.FloatOutput;
import ccre.ctrl.ExtendedMotor;
import ccre.ctrl.ExtendedMotorFailureException;
import edu.wpi.first.wpilibj.CANJaguar;

public class ExtendedJaguar extends ExtendedMotor implements FloatOutput {

    private final CANJaguar jaguar;
    private Boolean enableMode = null; // null until something cares. This means that it's not enabled, but could be automatically.

    public ExtendedJaguar(int deviceNumber) throws ExtendedMotorFailureException {
        try {
            jaguar = new CANJaguar(deviceNumber);
            jaguar.setPercentMode();
        } catch (RuntimeException ex) {
            throw new ExtendedMotorFailureException("WPILib CANJaguar Failure: Create", ex);
        }
    }

    @Override
    public void set(float value) throws ExtendedMotorFailureException {
        if (enableMode == null) {
            enable();
        }
        try {
            jaguar.set(value);
        } catch (RuntimeException ex) {
            throw new ExtendedMotorFailureException("WPILib CANJaguar Failure: Set", ex);
        }
    }

    @Override
    public void enable() throws ExtendedMotorFailureException {
        try {
            jaguar.enableControl();
        } catch (RuntimeException ex) {
            throw new ExtendedMotorFailureException("WPILib CANJaguar Failure: Enable", ex);
        }
        enableMode = true;
    }

    @Override
    public void disable() throws ExtendedMotorFailureException {
        try {
            jaguar.disableControl();
        } catch (RuntimeException ex) {
            throw new ExtendedMotorFailureException("WPILib CANJaguar Failure: Disable", ex);
        }
        enableMode = false;
    }

    @Override
    public BooleanOutput asEnable() throws ExtendedMotorFailureException {
        return new BooleanOutput() {
            public void set(boolean value) {
                if (enableMode == null || enableMode.booleanValue() != value) {
                    if (value) {
                        enable();
                    } else {
                        disable();
                    }
                }
            }
        };
    }

    public FloatOutput asMode(OutputControlMode mode) throws ExtendedMotorFailureException {
        try {
            switch (mode) {
            case CURRENT_FIXED_PID:
                jaguar.setCurrentMode(1, 0, 0);
                return this;
            case VOLTAGE_FIXED:
                jaguar.setVoltageMode();
                return this;
            case GENERIC_FRACTIONAL:
            case VOLTAGE_FRACTIONAL:
                jaguar.setPercentMode();
                return this;
            default:
                return null;
            }
        } catch (RuntimeException ex) {
            throw new ExtendedMotorFailureException("WPILib CANJaguar Failure: Set Mode", ex);
        }
    }

    @Override
    public boolean hasInternalPID() {
        return true;
    }

    @Override
    public void setInternalPID(float P, float I, float D) throws ExtendedMotorFailureException {
        try {
            jaguar.setPID(P, I, D);
        } catch (RuntimeException ex) {
            throw new ExtendedMotorFailureException("WPILib CANJaguar Failure: Set PID", ex);
        }
    }

    public FloatInputPoll asStatus(final StatusType type) {
        switch (type) {
        case BUS_VOLTAGE:
        case OUTPUT_CURRENT:
        case OUTPUT_VOLTAGE:
        case TEMPERATURE:
            return new FloatInputPoll() {
                public float get() {
                    try {
                        switch (type) {
                        case BUS_VOLTAGE:
                            return (float) jaguar.getBusVoltage();
                        case OUTPUT_VOLTAGE:
                            return (float) jaguar.getOutputVoltage();
                        case OUTPUT_CURRENT:
                            return (float) jaguar.getOutputCurrent();
                        case TEMPERATURE:
                            return (float) jaguar.getTemperature();
                        }
                    } catch (RuntimeException ex) {
                        throw new ExtendedMotorFailureException("WPILib CANJaguar Failure: Status", ex);
                    }
                    throw new ExtendedMotorFailureException("Invalid internal asStatus setting: " + type);
                }
            };
        default:
            return null;
        }
    }

    @Override
    public Object getDiagnostics(ExtendedMotor.DiagnosticType type) {
        try {
            switch (type) {
            case CAN_JAGUAR_FAULTS:
            case GENERIC_FAULT_MASK:
                return (long) jaguar.getFaults();
            case BUS_VOLTAGE_FAULT:
                return (jaguar.getFaults() & CANJaguar.kBusVoltageFault) != 0;
            case CURRENT_FAULT:
                return (jaguar.getFaults() & CANJaguar.kCurrentFault) != 0;
            case GATE_DRIVER_FAULT:
                return (jaguar.getFaults() & CANJaguar.kGateDriverFault) != 0;
            case TEMPERATURE_FAULT:
                return (jaguar.getFaults() & CANJaguar.kTemperatureFault) != 0;
            default:
                return null;
            }
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
