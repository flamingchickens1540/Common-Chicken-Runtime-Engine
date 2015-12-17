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
package org.team1540.example;

import ccre.channel.BooleanOutput;
import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.ctrl.Drive;
import ccre.frc.FRC;
import ccre.instinct.AutonomousModeOverException;
import ccre.instinct.InstinctModule;

/**
 * A slightly-more-complex Test program. This handles driving, shifting, the
 * compressor, and autonomous.
 *
 * @author skeggsc
 */
public class Test {

    /**
     * Set up the test robot. This includes tank drive, high gear/low gear, a
     * compressor, and a simple autonomous.
     */
    public void setupRobot() {
        // Driving
        FloatInput leftAxis = FRC.joystick1.axis(2);
        FloatInput rightAxis = FRC.joystick1.axis(5);
        final FloatOutput leftOut = FRC.talon(2, FRC.MOTOR_FORWARD, 0.1f);
        final FloatOutput rightOut = FRC.talon(1, FRC.MOTOR_REVERSE, 0.1f);
        Drive.tank(leftAxis, rightAxis, leftOut, rightOut);
        // Shifting
        BooleanOutput shifter = FRC.solenoid(2);
        shifter.setFalseWhen(FRC.startTele);
        shifter.setTrueWhen(FRC.joystick1.onPress(3));
        shifter.setFalseWhen(FRC.joystick1.onPress(1));
        // Compressor
        FRC.compressor(1, 1);
        // Autonomous
        FRC.registerAutonomous(new InstinctModule() {
            @Override
            protected void autonomousMain() throws AutonomousModeOverException, InterruptedException {
                leftOut.set(-1);
                rightOut.set(-1);
                waitForTime(5000);
                leftOut.set(0);
                rightOut.set(0);
            }
        });
    }
}
