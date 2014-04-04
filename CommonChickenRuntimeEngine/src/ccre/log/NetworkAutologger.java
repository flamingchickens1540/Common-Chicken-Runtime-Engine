/*
 * Copyright 2013-2014 Colby Skeggs
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
package ccre.log;

import ccre.cluck.*;
import ccre.concurrency.CollapsingWorkerThread;
import ccre.util.CArrayUtils;
import ccre.util.CHashMap;
import ccre.util.UniqueIds;

/**
 * A logging tool that shares all logging between networked cluck systems
 * automatically.
 *
 * @author skeggsc
 */
public final class NetworkAutologger implements LoggingTarget {

    /**
     * Whether or not a NetworkAutologger has been registered globally.
     */
    private static boolean registered = false;

    /**
     * Register a new global NetworkAutologger with the logging manager. This
     * only occurs once - after that an warning will be logged again.
     */
    public static void register() {
        if (registered) {
            Logger.warning("Network autologger registered twice!");
            return;
        }
        registered = true;
        Logger.addTarget(new NetworkAutologger(CluckGlobals.getNode()));
    }
    /**
     * The current list of remotes to send logging messages to.
     */
    private String[] remotes = new String[0];
    /**
     * The current cache of subscribed LoggingTargets to send logging messages
     * to.
     */
    private final CHashMap<String, LoggingTarget> targetCache = new CHashMap<String, LoggingTarget>();
    private static final LoggingTarget localLoggingTarget = new LoggingTarget() {
        public void log(LogLevel level, String message, Throwable throwable) {
            Logger.log(level, "[NET] " + message, throwable);
        }

        public void log(LogLevel level, String message, String extended) {
            Logger.logExt(level, "[NET] " + message, extended);
        }
    };

    /**
     * Internal use only. This rechecks the network for the current set of other
     * NetworkAutologgers.
     *
     * @param node The CluckNode to search on.
     * @param localpath The local path, to ignore in the remote list.
     * @throws InterruptedException If the thread is interrupted while searching
     * for remotes.
     */
    void autologgingRecheck(CluckNode node, String localpath) throws InterruptedException {
        Logger.fine("[LOCAL] Rechecking logging...");
        remotes = node.searchRemotes((int) CluckNode.RMT_LOGTARGET, 1000);
        Logger.config("[LOCAL] Requested loggers: " + CArrayUtils.asList(remotes));
        for (String s : remotes) {
            if (s.indexOf("auto-") != -1 && !localpath.equals(s) && targetCache.get(s) == null) {
                targetCache.put(s, node.subscribeLT(s, LogLevel.FINEST));
                Logger.config("[LOCAL] Loaded logger: " + s);
            }
        }
    }

    /**
     * Create a new NetworkAutologger hooked up to the specified node.
     *
     * @param node The node to attach to.
     */
    public NetworkAutologger(final CluckNode node) {
        final String here = UniqueIds.global.nextHexId();
        final String localpath = "auto-" + here;
        final CollapsingWorkerThread autologger = new CollapsingWorkerThread("network-autologger") {
            @Override
            protected void doWork() throws Throwable {
                autologgingRecheck(node, localpath);
            }
        };
        autologger.start();
        new CluckSubscriber(node) {
            @Override
            protected void receive(String source, byte[] data) {
            }

            @Override
            protected void receiveBroadcast(String source, byte[] data) {
                if (data.length == 1 && data[0] == CluckNode.RMT_NOTIFY) {
                    autologger.trigger();
                }
            }
        }.attach("netwatch-" + here);
        node.publish(localpath, localLoggingTarget);
    }

    public void log(LogLevel level, String message, Throwable throwable) {
        if (message.startsWith("[NET] ")) { // From the network, so don't broadcast.
            return;
        }
        if (message.startsWith("[LOCAL] ")) { // Should not be sent over the network.
            return;
        }
        String[] l = remotes;
        for (String cur : l) {
            LoggingTarget lt = targetCache.get(cur);
            if (lt != null) {
                lt.log(level, message, throwable);
            }
        }
    }

    public void log(LogLevel level, String message, String extended) {
        if (message.startsWith("[NET] ")) { // From the network, so don't broadcast.
            return;
        }
        if (message.startsWith("[LOCAL] ")) { // Should not be sent over the network.
            return;
        }
        String[] l = remotes;
        for (String cur : l) {
            LoggingTarget lt = targetCache.get(cur);
            if (lt != null) {
                lt.log(level, message, extended);
            }
        }
    }
}
