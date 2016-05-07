/*
 * Copyright 2013-2016 Cel Skeggs
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
package ccre.concurrency;

import java.io.InterruptedIOException;

import ccre.log.Logger;
import ccre.util.UniqueIds;
import ccre.verifier.FlowPhase;
import ccre.verifier.SuppressPhaseWarnings;

/**
 * A nice wrapper for the builtin Thread. Provides a system to prevent execution
 * of the run outside of the target thread, provides an easy way to name the
 * thread, provides a builtin handler for throwables, and provides an abstract
 * method for the body of the thread.
 *
 * @author skeggsc
 */
public abstract class ReporterThread extends Thread {

    private static final UniqueIds idGen = new UniqueIds();
    /**
     * Has the run method already been called?
     */
    private boolean started = false;
    /**
     * A lock for the started boolean.
     */
    private final Object startedLock = new Object();

    /**
     * Create a new ReporterThread. The passed name will have a unique ID
     * appended to it.
     *
     * @param name the name of this type of thread.
     */
    public ReporterThread(String name) {
        super(name + "-" + idGen.nextId());
    }

    @Override
    public final void run() throws IllegalStateException {
        if (this != Thread.currentThread()) {
            throw new IllegalStateException("Run function of Thread " + getName() + " called directly!");
        }
        synchronized (startedLock) {
            if (started) {
                throw new IllegalStateException("Run function of Thread " + getName() + " recalled!");
            }
            started = true;
        }
        try {
            threadBody();
        } catch (OutOfMemoryError oom) {
            System.err.println("OutOfMemory");
            Logger.severe("OutOfMemory");
            Logger.severe("OutOfMemory", oom); // in case we can
        } catch (InterruptedIOException ex) {
            Logger.warning("Interruption (during IO) of Thread " + this.getName(), ex);
        } catch (InterruptedException ex) {
            Logger.warning("Interruption of Thread " + this.getName(), ex);
        } catch (Throwable thr) {
            Logger.severe("Abrupt termination of Thread " + this.getName(), thr);
        }
    }

    @FlowPhase
    @SuppressPhaseWarnings // because it occurs at most once per thread, it's
                           // okay to run start() in flow mode
    public boolean startIfNotAlive() {
        if (isAlive()) {
            return false;
        }
        start();
        return true;
    }

    /**
     * The body of the thread. This will be called when the thread starts. This
     * is guaranteed to be called precisely once by the ReporterThread.
     *
     * @throws Throwable if something goes wrong. this will be caught by the
     * ReporterThread automatically.
     */
    protected abstract void threadBody() throws Throwable;
}
