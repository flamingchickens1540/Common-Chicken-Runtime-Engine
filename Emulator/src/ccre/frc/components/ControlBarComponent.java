/*
 * Copyright 2014-2016 Colby Skeggs
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
package ccre.frc.components;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import ccre.channel.FloatCell;
import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.frc.DeviceComponent;

/**
 * A bar that can be dragged around to modify a floating-point value
 *
 * @author skeggsc
 */
public class ControlBarComponent extends DeviceComponent implements FloatOutput {

    private boolean dragging = false;
    private int maxWidth = 0; // zero means no maximum
    private final FloatCell value;
    private final float min, max, originValue;

    /**
     * Create a ControlBarComponent with a certain minimum and maximum.
     *
     * @param min the value at the far left.
     * @param max the value at the far right.
     * @param defaultValue the default value.
     * @param originValue the origin value.
     */
    public ControlBarComponent(float min, float max, float defaultValue, float originValue) {
        this.max = max;
        this.min = min;
        this.value = new FloatCell(defaultValue);
        this.originValue = originValue;
    }

    @Override
    public int render(Graphics2D g, int width, int height, FontMetrics fontMetrics, int mouseX, int mouseY, int lastShift) {
        int startX = lastShift + 5;
        int startY = 5;
        int endX = width - 5;
        int endY = height - 5;
        int barWidth = endX - startX;
        if (maxWidth != 0 && barWidth > maxWidth) {
            barWidth = maxWidth;
            endX = startX + maxWidth;
        }
        int barHeight = endY - startY;
        int originX = Math.round(startX + barWidth * (originValue - min) / (max - min));
        g.setColor(Color.WHITE);
        g.drawRect(startX - 1, startY - 1, barWidth + 1, barHeight + 1);
        g.setColor(Color.RED);
        int actualLimitX = Math.round(startX + barWidth * (value.get() - min) / (max - min)) - originX;
        if (actualLimitX < 0) {
            g.fillRect(originX + actualLimitX, startY, -actualLimitX, barHeight);
        } else {
            g.fillRect(originX, startY, actualLimitX, barHeight);
        }
        hitzone = new Rectangle(startX, startY, endX - startX, endY - startY);
        return endX + 5;
    }

    /**
     * Set the maximum width for this component to take up, in pixels. Zero
     * means no maximum, which is the default.
     *
     * @param width the new maximum width.
     * @return this ControlBarComponent, for method chaining.
     */
    public ControlBarComponent setMaxWidth(int width) {
        this.maxWidth = width;
        return this;
    }

    @Override
    public void onPress(int x, int y) {
        dragging = true;
        onMouseMove(x, y);
    }

    @Override
    public void onMouseMove(int x, int y) {
        if (dragging) {
            Rectangle rect = hitzone.getBounds();
            value.safeSet(Math.min(1, Math.max(0, ((x - rect.x) / (float) rect.width))) * (max - min) + min);
            repaint();
        }
    }

    @Override
    public void onMouseExit(int x, int y) {
        dragging = false;
    }

    @Override
    public void onRelease(int x, int y) {
        dragging = false;
    }

    /**
     * Gets the current value set on this slider.
     *
     * @return the current value.
     */
    public float get() {
        return value.get();
    }

    /**
     * Provides a FloatInput representing the current value set on this slider.
     *
     * @return the FloatInput.
     */
    public FloatInput asInput() {
        return value;
    }

    @Override
    public void set(float value) {
        this.value.safeSet(value);
        repaint();
    }
}
