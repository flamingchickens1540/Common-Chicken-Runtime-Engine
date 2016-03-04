/*
 * Copyright 2013-2014 Cel Skeggs
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
package org.team1540.minimal;

import ccre.frc.FRCApplication;
import ccre.log.Logger;

/**
 * A very simple example program.
 *
 * @author skeggsc
 */
public class Minimal implements FRCApplication {

    /**
     * Set up the robot. For the minimal robot, this only means printing a
     * message.
     */
    public void setupRobot() {
        Logger.info("I live!");
    }
}
