/*
 * Copyright 2013 Vincent Miller
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
package ccre.obsidian.comms.test;

import ccre.chan.FloatInput;
import ccre.chan.FloatOutput;
import ccre.chan.FloatStatus;
import ccre.cluck.CluckNode;
import ccre.concurrency.ReporterThread;
import ccre.ctrl.Ticker;
import ccre.event.EventConsumer;
import ccre.event.EventLogger;
import ccre.log.LogLevel;
import ccre.log.Logger;
import ccre.obsidian.comms.ReliableCompressionCluckLink;
import java.util.Arrays;
import java.util.Random;

/**
 * Some code for testing the ReliableLink over a FakeBasicLink.
 *
 * @author skeggsc
 */
public class ReliabilityTest {

    public final FakeBasicLink fbl = new FakeBasicLink() {
        @Override
        protected void receiveAlphaEnd(byte[] bytes, int offset, int count) {
            alphaEnd.addBasicReceiveToQueue(Arrays.copyOfRange(bytes, offset, offset + count));
            //alphaEnd.basicReceiveHandler(bytes, offset, count);
        }

        @Override
        protected void receiveBetaEnd(byte[] bytes, int offset, int count) {
            betaEnd.addBasicReceiveToQueue(Arrays.copyOfRange(bytes, offset, offset + count));
            betaEnd.basicReceiveHandler(bytes, offset, count);
        }
    };
    public final CluckNode alphaNode = new CluckNode();
    public final ReliableCompressionCluckLink alphaEnd = new ReliableCompressionCluckLink(alphaNode, "beta") {
        @Override
        protected void basicStartComms() {
            Logger.info("Starting alpha: " + this);
        }

        @Override
        protected void basicTransmit(byte[] packet, int offset, int count) {
            fbl.sendAlphaEnd(packet, offset, count);
        }

        @Override
        public String toString() {
            return "[ALPHAEND]";
        }
    };
    public final CluckNode betaNode = new CluckNode();
    public final ReliableCompressionCluckLink betaEnd = new ReliableCompressionCluckLink(betaNode, "alpha") {
        @Override
        protected void basicStartComms() {
            Logger.info("Starting beta: " + this);
        }

        @Override
        protected void basicTransmit(byte[] packet, int offset, int count) {
            fbl.sendBetaEnd(packet, offset, count);
        }

        @Override
        public String toString() {
            return "[BETAEND]";
        }
    };

    public static void main(String[] args) throws InterruptedException {
        Logger.minimumLevel = LogLevel.INFO;
        Logger.info("Starting RT systems");
        final ReliabilityTest rt = new ReliabilityTest();
        rt.alphaNode.debugLogAll = rt.betaNode.debugLogAll = true;
        rt.startSubsystems();
        Logger.info("Delaying...");
        Thread.sleep(2000);
        Logger.info("Starting main threads");
        new ReporterThread("Alpha-Main") {
            @Override
            protected void threadBody() throws Throwable {
                //rt.alphaNode.publish("log-test", new EventLogger(LogLevel.INFO, "Log-Test on ALPHA!"));
                final FloatStatus ctr = new FloatStatus();
                new Ticker(100).addListener(new EventConsumer() {
                    Random r = new Random();

                    @Override
                    public void eventFired() {
                        ctr.writeValue((float) r.nextGaussian());
                    }
                });
                ctr.addTarget(new FloatOutput() {
                    @Override
                    public void writeValue(float value) {
                        Logger.info("Sent new value: " + value);
                    }
                });
                rt.alphaNode.publish("intest", (FloatInput) ctr);
                rt.alphaNode.publish("checker", new EventConsumer() {
                    long start = System.currentTimeMillis();
                    long last = start;

                    @Override
                    public void eventFired() {
                        long now = System.currentTimeMillis();
                        Logger.info("Received at " + (now - start) / 1000.0f + " = +" + (now - last));
                        if (now - last < 5) {
                            Logger.info("Double-up!");
                        }
                        last = now;
                    }
                });
            }
        }.start();
        new ReporterThread("Beta-Main") {
            @Override
            protected void threadBody() throws Throwable {
                //new Ticker(100).addListener(rt.betaNode.subscribeEC("alpha/checker"));
                rt.betaNode.subscribeFIP("alpha/unF/intest", false).addTarget(new FloatOutput() {
                    @Override
                    public void writeValue(float value) {
                        Logger.info("Got new value: " + value);
                    }
                });
                //rt.betaNode.subscribeEC("alpha/beta/log-test").eventFired();
            }
        }.start();
    }

    public void startSubsystems() {
        fbl.start();
        alphaEnd.start();
        betaEnd.start();
    }
}