/*
 * Copyright 2013-2015 Cel Skeggs
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
package ccre.tuning;

import ccre.channel.BooleanCell;
import ccre.channel.EventOutput;
import ccre.channel.FloatCell;
import ccre.cluck.Cluck;
import ccre.cluck.CluckNode;
import ccre.cluck.CluckPublisher;
import ccre.log.Logger;
import ccre.storage.Storage;
import ccre.storage.StorageSegment;
import ccre.util.UniqueIds;
import ccre.verifier.FlowPhase;
import ccre.verifier.SetupPhase;
import ccre.verifier.SuppressPhaseWarnings;

/**
 * A TuningContext represents a context in which variables can be saved and
 * published to the network.
 *
 * @author skeggsc
 */
public final class TuningContext {

    /**
     * The node to publish the values to.
     */
    private final CluckNode node;
    /**
     * The segment to store the values in.
     */
    private final StorageSegment segment;

    /**
     * Create a new TuningContext from a specified CluckNode and name of storage
     * (used to find the StorageSegment)
     *
     * @param node the CluckNode to share values over.
     * @param storageName the storage name to save values to.
     */
    public TuningContext(CluckNode node, String storageName) {
        this(node, Storage.openStorage(storageName));
    }

    /**
     * Create a new TuningContext from a specified CluckNode and a specified
     * StorageSegment.
     *
     * @param enc the CluckNode to share values over.
     * @param seg the segment to save values to.
     */
    public TuningContext(CluckNode enc, StorageSegment seg) {
        this.node = enc;
        this.segment = seg;
    }

    /**
     * Create a new TuningContext from the global CluckNode and name of storage
     * (used to find the StorageSegment)
     *
     * @param storageName the storage name to save values to.
     */
    public TuningContext(String storageName) {
        this(Cluck.getNode(), Storage.openStorage(storageName));
    }

    /**
     * Create a new TuningContext from the global CluckNode and a specified
     * StorageSegment.
     *
     * @param seg the segment to save values to.
     */
    public TuningContext(StorageSegment seg) {
        this(Cluck.getNode(), seg);
    }

    /**
     * Get the attached Cluck node.
     *
     * @return the CluckNode behind this TuningContext.
     */
    public CluckNode getNode() {
        return node;
    }

    /**
     * Get the attached storage segment.
     *
     * @return the storage segment behind this TuningContext.
     */
    public StorageSegment getSegment() {
        return segment;
    }

    /**
     * Get a FloatCell with the specified name and default value. This will be
     * tunable over the network and saved on the roboRIO once flush() is called.
     *
     * @param name the name of the tunable value.
     * @param default_ the default value.
     * @return the FloatCell representing the current value.
     */
    public FloatCell getFloat(String name, float default_) {
        FloatCell out = new FloatCell(default_);
        segment.attachFloatHolder(name, out);
        CluckPublisher.publish(node, name, out);
        return out;
    }

    /**
     * Get a BooleanCell with the specified name and default value. This will be
     * tunable over the network and saved on the roboRIO once flush() is called.
     *
     * @param name the name of the tunable value.
     * @param default_ the default value.
     * @return the BooleanCell representing the current value.
     */
    public BooleanCell getBoolean(String name, boolean default_) {
        BooleanCell out = new BooleanCell(default_);
        segment.attachBooleanHolder(name, out);
        CluckPublisher.publish(node, name, out);
        return out;
    }

    /**
     * Flush the StorageSegment - save the current value.
     */
    @SetupPhase
    public void flush() {
        segment.flush();
        Logger.info("Flushed storage segment " + segment.getName());
    }

    /**
     * Get an event that flushes this object.
     *
     * @return the EventOutput that will flush this object.
     * @see #flush()
     */
    @SetupPhase
    public EventOutput getFlushEvent() {
        return new EventOutput() {
            @Override
            @SuppressPhaseWarnings // TODO: rather than ignoring this issue,
                                   // have a worker thread take care of it.
            public void event() {
                flush();
            }
        };
    }

    /**
     * Publish an EventOutput that can be used to save the tuning variables on
     * this context.
     *
     * @param name The name for the EventOutput to be published under. (Prefixed
     * by "Save Tuning for ".)
     * @return This TuningContext. Returned for method chaining purposes.
     */
    public TuningContext publishSavingEvent(String name) {
        CluckPublisher.publish(node, "Save Tuning for " + name, getFlushEvent());
        return this;
    }

    /**
     * Publish an EventOutput that can be used to save the tuning variables on
     * this context. The name will be "Save Tuning for " followed by the name of
     * the StorageSegment that this context uses. If no name is available,
     * "anonymous-N" will be used instead, where N is an arbitrary number.
     *
     * @return This TuningContext. Returned for method chaining purposes.
     */
    public TuningContext publishSavingEvent() {
        String name = segment.getName();
        return publishSavingEvent(name == null ? UniqueIds.global.nextHexId("anonymous") : name);
    }
}
