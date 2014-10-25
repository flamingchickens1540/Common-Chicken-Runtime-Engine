/*
 * Copyright 2014 Colby Skeggs.
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
package ccre.supercanvas.components.channels;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import ccre.channel.BooleanInput;
import ccre.channel.BooleanOutput;
import ccre.channel.BooleanStatus;
import ccre.supercanvas.BaseChannelComponent;

/**
 * A component allowing interaction with booleans.
 *
 * @author skeggsc
 */
public class BooleanControlComponent extends BaseChannelComponent implements BooleanInput {

    private static final long serialVersionUID = 3529467636546288860L;
    private final BooleanStatus pressed = new BooleanStatus();

    /**
     * Create a new BooleanControlComponent with a BooleanOutput to control.
     *
     * @param cx the X coordinate.
     * @param cy the Y coordinate.
     * @param name the name of the output.
     * @param out the BooleanOutput to control.
     */
    public BooleanControlComponent(int cx, int cy, String name, BooleanOutput out) {
        super(cx, cy, name);
        pressed.send(out);
    }

    /**
     * Create a new BooleanControlComponent.
     *
     * @param cx the X coordinate.
     * @param cy the Y coordinate.
     * @param name the name of the output.
     */
    public BooleanControlComponent(int cx, int cy, String name) {
        super(cx, cy, name);
    }

    @Override
    protected boolean containsForInteract(int x, int y) {
        return x >= centerX - 30 && x <= centerX + 30 && y >= centerY - 20 && y <= centerY + 30;
    }

    @Override
    public void channelRender(Graphics2D g, int screenWidth, int screenHeight, FontMetrics fontMetrics, int mouseX, int mouseY) {
        AffineTransform origO = g.getTransform();
        boolean isPressed = this.pressed.get();
        {
            g.setColor(isPressed ? Color.GREEN.darker() : Color.RED.darker());
            AffineTransform orig = g.getTransform();
            g.rotate(isPressed ? 10 : -10, centerX + (isPressed ? 3 : -3), centerY + 10);
            g.fillRect(centerX - 5, centerY + 5, 10, 45);
            g.setTransform(orig);
            g.setColor(Color.GRAY.darker().darker());
            g.fillRect(centerX - 20, centerY + 10, 40, 20);
        }
        g.translate(-5, 2);
        {
            g.setColor(isPressed ? Color.GREEN : Color.RED);
            AffineTransform orig = g.getTransform();
            g.rotate(isPressed ? 10 : -10, centerX + (isPressed ? 3 : -3), centerY + 10);
            g.fillRect(centerX - 5, centerY + 5, 10, 45);
            g.setTransform(orig);
            g.setColor(Color.GRAY.darker());
            g.fillRect(centerX - 20, centerY + 10, 40, 20);
        }
        g.setTransform(origO);
    }

    @Override
    public boolean onInteract(int x, int y) {
        pressed.set(!pressed.get());
        return true;
    }

    @Override
    public void send(BooleanOutput output) {
        pressed.send(output);
    }

    @Override
    public void unsend(BooleanOutput output) {
        pressed.unsend(output);
    }

    @Override
    public boolean get() {
        return pressed.get();
    }
}