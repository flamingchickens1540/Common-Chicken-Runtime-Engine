/*
 * Copyright 2015 Cel Skeggs
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
package ccre.time;

import java.util.LinkedList;

/**
 * A "fake" implementation of time, in which the current time is controlled by
 * the {@link #forward(long)} method of this instance.
 *
 * WARNING: this contains complex and likely slightly broken synchronization
 * code. Do not use it in production!
 *
 * A time may be specified so that transitioning between fake time and real time
 * can be made seamless to the users.
 *
 * @author skeggsc
 */
public class FakeTime extends Time {

    private static final boolean debug = false;

    private long now = 0;
    private int adds = 0;
    private final LinkedList<Object> otherSleepers = new LinkedList<>();

    /**
     * Fast-forward time by the specified number of milliseconds, including
     * waiting to attempt to synchronize threads.
     *
     * @param millis how far forward to send time.
     * @throws InterruptedException if the thread is interrupted while
     * synchronizing with other threads.
     */
    public void forward(long millis) throws InterruptedException {
        if (closing) {
            throw new IllegalStateException("The FakeTime is closing! Don't try to control it!");
        }
        if (millis <= 0) {
            throw new IllegalArgumentException("FakeTime.forward expects a positive delta!");
        }
        synchronized (this) {
            now += millis;
            this.notifyAll();
        }
        Object[] osl;
        synchronized (this) {
            osl = otherSleepers.toArray();
            adds = 0;
        }
        for (Object obj : osl) {
            synchronized (obj) {
                obj.notifyAll();
            }
        }
        synchronized (this) {
            int i = 0;
            while (true) {
                if (adds >= osl.length) {
                    if (debug) {
                        if (i != 1) {
                            System.out.println("Completed in " + i + "!");
                        }
                    }
                    break;
                }
                this.wait(1);
                if (i++ >= 30) {
                    if (debug) {
                        System.out.println("Timed out!");
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected synchronized long nowMillis() {
        return now;
    }

    @Override
    protected synchronized long nowNanos() {
        return now * 1000000;
    }

    @Override
    protected synchronized void sleepFor(long millis) throws InterruptedException {
        if (closing) {
            throw new IllegalStateException("This FakeTime instance is shutting down! Don't try to wait on it!");
        }
        long target = now + millis;
        while (now < target) {
            if (closing) {
                throw new RuntimeException("Time provider started closing during sleep!");
            }
            this.wait();
        }
    }

    @Override
    protected void waitOn(Object object, long timeout) throws InterruptedException {
        if (closing) {
            throw new IllegalStateException("This FakeTime instance is shutting down! Don't try to wait on it!");
        }
        if (object == null) {
            throw new NullPointerException();
        }
        if (!Thread.holdsLock(object)) {
            throw new IllegalMonitorStateException("Thread does not hold lock for object!");
        }
        if (timeout == 0) {
            object.wait();
            return;
        } else if (timeout < 0) {
            throw new IllegalArgumentException("Negative wait time!");
        }
        // we ignore the actual timeout... we can't tell the difference between
        // an actual notification and a time-update notification!
        // so we just go with the spurious wakeups every time the time changes.
        synchronized (this) {
            otherSleepers.add(object);
            adds++;
        }
        try {
            // we only wait ONCE ... which means that we WILL have spurious
            // wakeups! I _do_ hope that code using this can handle them like
            // it's supposed to.

            // there's no race condition here because the notifying have to
            // synchronize with 'object' to be able to send our own
            // notification, and THIS thread is holding it.
            object.wait(1000);
            // but there is a possible starvation condition, if a later object
            // needs to be notified for the lock on this to be able to be
            // release
            // so we set a timeout on object, which should break the starvation
            // possibilities... eventually. (but long enough away to be noticed
            // by the user.)
            // this would be a bigger issue if it could ever happen in
            // production, so I'm leaving it for now.
            // simply put: NEVER USE FakeTime IN A PRODUCTION SYSTEM!
        } finally {
            synchronized (this) {
                otherSleepers.remove(object);
            }
        }
    }

    private boolean closing = false;

    @Override
    protected void close() {
        Object[] others;
        synchronized (this) {
            closing = true;
            others = otherSleepers.toArray();
            otherSleepers.clear();
        }
        // wake up, everyone!
        for (Object o : others) {
            synchronized (o) {
                o.notifyAll();
            }
        }
        synchronized (this) {
            now = 0;
            closing = false;
            // just in case something's still waiting on us
            this.notifyAll();
        }
    }
}
