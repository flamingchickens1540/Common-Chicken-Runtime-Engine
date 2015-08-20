/*
 * Copyright 2015 Colby Skeggs
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
package ccre.channel;

import ccre.concurrency.ConcurrentDispatchArray;

public abstract class DerivedBooleanInput extends DerivedUpdate implements BooleanInput {

    private boolean value;
    private final ConcurrentDispatchArray<EventOutput> consumers = new ConcurrentDispatchArray<>();

    public DerivedBooleanInput(UpdatingInput... updates) {
        super(updates);
        value = apply();
    }

    public DerivedBooleanInput(UpdatingInput[] updates, UpdatingInput... moreUpdates) {
        super(updates, moreUpdates);
        value = apply();
    }

    @Override
    protected final void update() {
        boolean newvalue = apply();
        if (newvalue != value) {
            value = newvalue;
            for (EventOutput consumer : consumers) {
                consumer.event();
            }
        }
    }

    protected final boolean updateWithRecovery() {
        boolean newvalue = apply();
        boolean recovered = false;
        if (newvalue != value) {
            value = newvalue;
            for (EventOutput consumer : consumers) {
                recovered |= consumer.eventWithRecovery();
            }
        }
        return recovered;
    }

    public final boolean get() {
        return value;
    }

    protected abstract boolean apply();

    @Override
    public void onUpdate(EventOutput notify) {
        consumers.add(notify);
    }

    @Override
    public EventOutput onUpdateR(EventOutput notify) {
        return consumers.addR(notify);
    }
}
