/*
 * Copyright 2013-2015 Colby Skeggs
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
package ccre.igneous;

import ccre.channel.EventInput;
import ccre.ctrl.AbstractJoystick;
import ccre.log.Logger;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.KinectStick;

/**
 * An IJoystick implementation that allows reading from a joystick on the driver
 * station.
 *
 * @author skeggsc
 */
final class CJoystick extends AbstractJoystick {

    /**
     * The joystick object that is read from.
     */
    private final GenericHID joy;

    /**
     * Create a new CJoystick for a specific joystick ID.
     *
     * @param joystick the joystick ID
     */
    CJoystick(int joystick, EventInput check) {
        super(check, 6, 12);
        if (joystick == 5) {
            joy = new KinectStick(1);
        } else if (joystick == 6) {
            joy = new KinectStick(2);
        } else {
            joy = new Joystick(joystick);
        }
    }

    @Override
    protected boolean getButton(int btn) {
        return joy.getRawButton(btn);
    }

    @Override
    protected float getAxis(int axis) {
        return (float) joy.getRawAxis(axis);
    }

    @Override
    protected boolean getPOV(int direction) {
        Logger.warning("POVs not supported on cRIO!");
        return false;
    }
}
