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
package ccre.cluck;

import ccre.channel.BooleanInput;
import ccre.channel.BooleanOutput;
import ccre.channel.BooleanStatus;
import ccre.channel.EventInput;
import ccre.channel.EventOutput;
import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.channel.FloatStatus;
import ccre.cluck.tcp.CluckTCPClient;
import ccre.cluck.tcp.CluckTCPServer;
import ccre.log.LogLevel;
import ccre.log.LoggingTarget;
import java.io.OutputStream;

/**
 * A storage location for the current CluckNode, CluckTCPServer, and
 * CluckTCPClient.
 *
 * @author skeggsc
 */
public final class Cluck {

    /**
     * The current CluckNode.
     */
    private static final CluckNode node = new CluckNode();
    /**
     * The current CluckTCPServer.
     */
    private static CluckTCPServer server;
    /**
     * The current CluckTCPClient.
     */
    private static CluckTCPClient client;

    /**
     * Get the current global CluckNode.
     *
     * @return The global CluckNode.
     */
    public static synchronized CluckNode getNode() {
        return node;
    }

    /**
     * Get the current global CluckTCPServer.
     *
     * @return The global CluckTCPServer.
     */
    public static synchronized CluckTCPServer getServer() {
        return server;
    }

    /**
     * Get the current global CluckTCPClient.
     *
     * @return The global CluckTCPClient.
     */
    public static synchronized CluckTCPClient getClient() {
        return client;
    }

    /**
     * Set up a server on the default port.
     */
    public static synchronized void setupServer() { // TODO: Is this needed?
        if (server != null) {
            throw new IllegalStateException("Server already set up!");
        }
        server = new CluckTCPServer(node);
        server.start();
    }

    /**
     * Set up a client pointing at the specified remote address, with the
     * specified name for this link and hint for what the remote end should call
     * this link.
     *
     * @param remote The remote address.
     * @param linkName The local link name.
     * @param hintedRemoteName The hint for what the remote server should call
     * this.
     */
    public static synchronized void setupClient(String remote, String linkName, String hintedRemoteName) {
        if (client != null) {
            throw new IllegalStateException("Client already set up!");
        }
        client = new CluckTCPClient(remote, node, linkName, hintedRemoteName);
        client.start();
    }

    /**
     * Publish an EventConsumer on the network.
     *
     * @param name The name for the EventConsumer.
     * @param consumer The EventConsumer.
     */
    public static void publish(String name, final EventOutput consumer) {
        CluckPublisher.publish(node, name, consumer);
    }

    /**
     * Subscribe to an EventConsumer from the network at the specified path.
     *
     * @param path The path to subscribe to.
     * @return the EventConsumer.
     */
    public static EventOutput subscribeEC(final String path) {
        return CluckPublisher.subscribeEC(node, path);
    }

    /**
     * Publish an EventSource on the network.
     *
     * @param name The name for the EventSource.
     * @param source The EventSource.
     */
    public static void publish(final String name, EventInput source) {
        CluckPublisher.publish(node, name, source);
    }

    /**
     * Subscribe to an EventSource from the network at the specified path.
     *
     * @param path The path to subscribe to.
     * @return the EventSource.
     */
    public static EventInput subscribeES(final String path) {
        return CluckPublisher.subscribeES(node, path);
    }

    /**
     * Publish a LoggingTarget on the network.
     *
     * @param name The name for the LoggingTarget.
     * @param lt The LoggingTarget.
     */
    public static void publish(String name, final LoggingTarget lt) {
        CluckPublisher.publish(node, name, lt);
    }

    /**
     * Subscribe to a LoggingTarget from the network at the specified path, with
     * only sending data for at least a minimum logging level.
     *
     * @param path The path to subscribe to.
     * @param minimum The minimum logging level to send over the network.
     * @return the LoggingTarget.
     */
    public static LoggingTarget subscribeLT(final String path, final LogLevel minimum) {
        return CluckPublisher.subscribeLT(node, path, minimum);
    }

    /**
     * Publish a BooleanInput on the network. This will send values to clients
     * when they connect.
     *
     * @param name The name for the BooleanInput.
     * @param input The BooleanInput.
     */
    public static void publish(final String name, final BooleanInput input) {
        CluckPublisher.publish(node, name, input);
    }

    /**
     * Subscribe to a BooleanInput from the network at the specified path.
     *
     * @param path The path to subscribe to.
     * @param shouldSubscribeByDefault Should this request the value from the
     * remote by default, as opposed to waiting until this is needed. If this is
     * false, then readValue() won't work until you run addTarget().
     * @return the BooleanInput.
     */
    public static BooleanInput subscribeBI(final String path, boolean shouldSubscribeByDefault) {
        return CluckPublisher.subscribeBI(node, path, shouldSubscribeByDefault);
    }

    /**
     * Publish a BooleanOutput on the network.
     *
     * @param name The name for the BooleanOutput.
     * @param output The BooleanOutput.
     */
    public static void publish(String name, final BooleanOutput output) {
        CluckPublisher.publish(node, name, output);
    }

    /**
     * Subscribe to a BooleanOutput from the network at the specified path.
     *
     * @param path The path to subscribe to.
     * @return the BooleanOutput.
     */
    public static BooleanOutput subscribeBO(final String path) {
        return CluckPublisher.subscribeBO(node, path);
    }

    /**
     * Publish a FloatInput on the network. This will send values to clients
     * when they connect.
     *
     * @param name The name for the FloatInput.
     * @param input The FloatInput.
     * @see #publish(java.lang.String, ccre.chan.FloatInputProducer)
     */
    public static void publish(final String name, final FloatInput input) {
        CluckPublisher.publish(node, name, input);
    }

    /**
     * Subscribe to a FloatInput from the network at the specified path.
     *
     * @param path The path to subscribe to.
     * @param subscribeByDefault Should this request the value from the remote
     * by default, as opposed to waiting until this is needed. If this is false,
     * then readValue() won't work until you run addTarget().
     * @return the FloatInput.
     */
    public static FloatInput subscribeFI(final String path, boolean subscribeByDefault) {
        return CluckPublisher.subscribeFI(node, path, subscribeByDefault);
    }

    /**
     * Publish a FloatOutput on the network.
     *
     * @param name The name for the FloatOutput.
     * @param out The FloatOutput.
     */
    public static void publish(String name, final FloatOutput out) {
        CluckPublisher.publish(node, name, out);
    }

    /**
     * Subscribe to a FloatOutput from the network at the specified path.
     *
     * @param path The path to subscribe to.
     * @return the FloatOutput.
     */
    public static FloatOutput subscribeFO(final String path) {
        return CluckPublisher.subscribeFO(node, path);
    }

    /**
     * Publish a FloatStatus on the network.
     *
     * No corresponding subscribe is provided yet.
     *
     * @param name The name for the FloatStatus.
     * @param tune The FloatStatus.
     */
    public static void publish(final String name, final FloatStatus tune) {
        CluckPublisher.publish(node, name, tune);
    }

    /**
     * Publish a BooleanStatus on the network.
     *
     * No corresponding subscribe is provided yet.
     *
     * @param name The name for the BooleanStatus.
     * @param stat The BooleanStatus to publish.
     */
    public static void publish(final String name, BooleanStatus stat) {
        CluckPublisher.publish(node, name, stat);
    }

    /**
     * Publish an OutputStream on the network.
     *
     * @param name The name for the OutputStream.
     * @param out The OutputStream.
     */
    public static void publish(String name, final OutputStream out) {
        CluckPublisher.publish(node, name, out);
    }

    /**
     * Subscribe to an OutputStream from the network at the specified path.
     *
     * @param path The path to subscribe to.
     * @return the OutputStream.
     */
    public static OutputStream subscribeOS(final String path) {
        return CluckPublisher.subscribeOS(node, path);
    }

    private Cluck() {
    }
}