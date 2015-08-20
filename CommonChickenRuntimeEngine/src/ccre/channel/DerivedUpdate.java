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

public abstract class DerivedUpdate {

    public DerivedUpdate(UpdatingInput[] updates, UpdatingInput... moreUpdates) {
        EventOutput doUpdate = new EventOutput() {
            @Override
            public void event() {
                update();
            }

            @Override
            public boolean eventWithRecovery() {
                return updateWithRecovery();
            }
        };
        if (updates.length == 0) {
            throw new IllegalArgumentException("Must be at least one update source!");
        }
        for (int i = 0; i < updates.length; i++) {
            if (updates[i] == null) {
                throw new NullPointerException();
            }
            updates[i].onUpdate(doUpdate);
        }
    }

    public DerivedUpdate(UpdatingInput... updates) {
        this(updates, new UpdatingInput[0]);
    }

    protected abstract void update();

    protected boolean updateWithRecovery() {
        update();
        return false;
    }
}
