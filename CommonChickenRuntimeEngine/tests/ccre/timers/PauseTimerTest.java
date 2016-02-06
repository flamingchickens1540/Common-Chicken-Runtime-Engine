/*
 * Copyright 2016 Colby Skeggs
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
package ccre.timers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ccre.channel.FloatCell;
import ccre.log.LogLevel;
import ccre.log.VerifyingLogger;
import ccre.testing.CountingEventOutput;
import ccre.time.FakeTime;
import ccre.time.Time;

@SuppressWarnings("javadoc")
public class PauseTimerTest {

    private static Time oldProvider;
    private static FakeTime fake;
    private PauseTimer pt;

    @BeforeClass
    public static void setUpClass() {
        assertNull(oldProvider);
        oldProvider = Time.getTimeProvider();
        fake = new FakeTime();
        Time.setTimeProvider(fake);
    }

    @AfterClass
    public static void tearDownClass() {
        assertNotNull(oldProvider);
        Time.setTimeProvider(oldProvider);
        oldProvider = null;
        fake = null;
    }

    @Before
    public void setUp() throws Exception {
        pt = new PauseTimer(1000);
        VerifyingLogger.begin();
    }

    @After
    public void tearDown() throws Exception {
        VerifyingLogger.checkAndEnd();
        pt.terminate();
        pt = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPauseTimerZero() {
        new PauseTimer(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPauseTimerNegative() {
        new PauseTimer(-1);
    }

    @Test
    public void testPauseTimerSmallPositive() {
        new PauseTimer(1);
    }

    @Test(expected = NullPointerException.class)
    public void testPauseTimerNull() {
        new PauseTimer(null);
    }

    @Test
    public void testNoDefaultTrigger() throws InterruptedException {
        CountingEventOutput con = new CountingEventOutput();
        CountingEventOutput coff = new CountingEventOutput();
        pt.triggerAtChanges(con, coff);
        fake.forward(700);
        fake.forward(700);
        fake.forward(700);
        // if it ever fires here, then ceo will get annoyed
    }

    @Test
    public void testEventAllAtOnce() throws InterruptedException {
        CountingEventOutput ceo = new CountingEventOutput();
        pt.onRelease().send(ceo);
        fake.forward(2000);
        startEvt();
        ceo.ifExpected = true;
        fake.forward(1000);
        ceo.check();
    }

    @Test
    public void testTriggerVariance() throws InterruptedException {
        CountingEventOutput coff = new CountingEventOutput();
        FloatCell time = new FloatCell(1.0f);
        pt = new PauseTimer(time);
        pt.triggerAtEnd(coff);
        for (int i = 10; i < 3000; i *= 2) {
            time.set(i / 1000f);
            pt.event();
            Thread.sleep(2);
            fake.forward(i - 5);
            Thread.sleep(2);
            coff.ifExpected = true;
            fake.forward(5);
            Thread.sleep(2);
            coff.check();
        }
    }

    @Test
    public void testTriggerAtEnd() throws InterruptedException {
        CountingEventOutput coff = new CountingEventOutput();
        pt.triggerAtEnd(coff);
        startEvt();
        fake.forward(990);
        coff.ifExpected = true;
        fake.forward(10);
        coff.check();
    }

    @Test
    public void testTriggerAtStart() throws InterruptedException {
        CountingEventOutput con = new CountingEventOutput();
        pt.triggerAtStart(con);
        con.ifExpected = true;
        startEvt();
        con.check();
        fake.forward(1000);
    }

    @Test
    public void testTriggerAtChanges() throws InterruptedException {
        CountingEventOutput con = new CountingEventOutput();
        CountingEventOutput coff = new CountingEventOutput();
        pt.triggerAtChanges(con, coff);
        fake.forward(2000);
        con.ifExpected = true;
        startEvt();
        con.check();
        fake.forward(990);
        coff.ifExpected = true;
        fake.forward(10);
        coff.check();
    }

    @Test
    public void testEventSequenceMultiPress() throws InterruptedException {
        CountingEventOutput con = new CountingEventOutput();
        CountingEventOutput coff = new CountingEventOutput();
        pt.triggerAtChanges(con, coff);
        fake.forward(2000);
        con.ifExpected = true;
        startEvt();
        con.check();
        fake.forward(500);
        startEvt();
        fake.forward(990);
        coff.ifExpected = true;
        fake.forward(10);
        coff.check();
    }

    @Test
    public void testGet() throws InterruptedException {
        assertFalse(pt.get());
        startEvt();
        assertTrue(pt.get());
        for (int i = 0; i < 11; i++) {
            fake.forward(90);
            assertTrue(pt.get());
        }
        fake.forward(10);
        assertFalse(pt.get());
    }

    private void startEvt() throws InterruptedException {
        pt.event();
        Thread.sleep(2);
    }

    @Test
    public void testGetMultiEvent() throws InterruptedException {
        assertFalse(pt.get());
        startEvt();
        assertTrue(pt.get());
        for (int i = 0; i < 11; i++) {
            fake.forward(90);
            assertTrue(pt.get());
        }
        startEvt();
        assertTrue(pt.get());
        for (int i = 0; i < 11; i++) {
            fake.forward(90);
            assertTrue(pt.get());
        }
        fake.forward(10);
        assertFalse(pt.get());
    }

    @Test
    public void testStartError() {
        CountingEventOutput ceo = new CountingEventOutput();
        RuntimeException rtex = new RuntimeException("Purposeful failure.");
        pt.triggerAtStart(() -> {
            ceo.event();
            // TODO: test logging
            throw rtex;
        });
        ceo.ifExpected = true;
        VerifyingLogger.configure(LogLevel.SEVERE, "Error in PauseTimer notification", rtex);
        pt.event();
        VerifyingLogger.check();
        ceo.check();
    }

    @Test
    public void testEndError() throws InterruptedException {
        CountingEventOutput ceo = new CountingEventOutput();
        RuntimeException rtex = new RuntimeException("Purposeful failure.");
        pt.triggerAtEnd(() -> {
            ceo.event();
            // TODO: test logging
            throw rtex;
        });
        for (int i = 0; i < 10; i++) {
            // if something breaks internally, this loop will stop succeeding.
            pt.event();
            Thread.sleep(2);
            fake.forward(990);
            Thread.sleep(2);
            ceo.ifExpected = true;
            VerifyingLogger.configure(LogLevel.SEVERE, "Error in PauseTimer notification", rtex);
            Thread.sleep(2);
            fake.forward(10);
            Thread.sleep(2);
            ceo.check(); // flaky; 5 failures.
            VerifyingLogger.check();
        }
    }
}
