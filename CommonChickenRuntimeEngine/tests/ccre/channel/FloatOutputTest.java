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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ccre.testing.CountingFloatOutput;
import ccre.time.FakeTime;
import ccre.time.Time;
import ccre.util.Utils;
import ccre.util.Values;

@SuppressWarnings("javadoc")
public class FloatOutputTest {

    private final FloatOutput evil = (v) -> {
        throw new NoSuchElementException("safeSet purposeful failure.");
    };
    private CountingFloatOutput cfo1, cfo2;
    private FloatCell fs;

    private static Time oldProvider;
    private static FakeTime fake;

    // These two are for derivative testing.
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
        fs = new FloatCell();
        cfo1 = new CountingFloatOutput();
        cfo2 = new CountingFloatOutput();
    }

    @After
    public void tearDown() throws Exception {
        fs = null;
        cfo1 = null;
        cfo2 = null;
    }

    @Test
    public void testIgnored() {
        FloatOutput.ignored.set(0);
    }

    @Test
    public void testGetSetEventFloat() {
        for (float f : Values.interestingFloats) {
            EventOutput setE = cfo1.eventSet(f);
            cfo1.ifExpected = true;
            cfo1.valueExpected = f;
            setE.event();
            cfo1.check();
        }
    }

    @Test
    public void testGetSetEventFloatInput() {
        EventOutput setE = cfo1.eventSet(fs);
        for (float f : Values.interestingFloats) {
            fs.set(f);
            cfo1.ifExpected = true;
            cfo1.valueExpected = f;
            setE.event();
            cfo1.check();
        }
    }

    @Test(expected = NullPointerException.class)
    public void testGetSetEventFloatInputNull() {
        cfo1.eventSet(null);
    }

    @Test
    public void testSetWhenFloatEventInput() {
        for (float f : Values.interestingFloats) {
            EventCell es = new EventCell();
            cfo1.setWhen(f, es);
            cfo1.ifExpected = true;
            cfo1.valueExpected = f;
            es.event();
            cfo1.check();
        }
    }

    @Test(expected = NullPointerException.class)
    public void testSetWhenFloatEventInputNull() {
        cfo1.setWhen(0, null);
    }

    @Test
    public void testSetWhenFloatInputEventInput() {
        EventCell setE = new EventCell();
        cfo1.setWhen(fs, setE);
        for (float f : Values.interestingFloats) {
            fs.set(f);
            cfo1.ifExpected = true;
            cfo1.valueExpected = f;
            setE.event();
            cfo1.check();
        }
    }

    @Test(expected = NullPointerException.class)
    public void testSetWhenFloatInputEventInputNullA() {
        cfo1.setWhen(null, EventInput.never);
    }

    @Test(expected = NullPointerException.class)
    public void testSetWhenFloatInputEventInputNullB() {
        cfo1.setWhen(FloatInput.zero, null);
    }

    @Test
    public void testCombine() {
        FloatOutput combine = cfo1.combine(cfo2);
        for (float f : Values.interestingFloats) {
            cfo1.ifExpected = cfo2.ifExpected = true;
            cfo1.valueExpected = cfo2.valueExpected = f;
            combine.set(f);
            cfo1.check();
            cfo2.check();
        }
    }

    @Test(expected = NullPointerException.class)
    public void testCombineNull() {
        cfo1.combine(null);
    }

    @Test
    public void testNegate() {
        FloatOutput neg = cfo1.negate();
        for (float f : Values.interestingFloats) {
            cfo1.ifExpected = true;
            cfo1.valueExpected = -f;
            neg.set(f);
            cfo1.check();
        }
    }

    @Test
    public void testOutputDeadzone() {
        for (float zone : Values.lessInterestingFloats) {
            if (!Float.isFinite(zone) || zone < 0) {
                continue;
            }
            FloatOutput fout = cfo1.outputDeadzone(zone);
            for (float v : Values.lessInterestingFloats) {
                if (Float.isNaN(v)) {
                    continue;
                }
                if (Math.abs(v) < Math.abs(zone)) {
                    cfo1.valueExpected = 0;
                } else {
                    cfo1.valueExpected = v;
                }
                cfo1.ifExpected = true;
                fout.set(v);
                cfo1.check();
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOutputDeadzoneNaN() {
        cfo1.outputDeadzone(Float.NaN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOutputDeadzoneNegative() {
        cfo1.outputDeadzone(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOutputDeadzoneInfinity() {
        cfo1.outputDeadzone(Float.POSITIVE_INFINITY);
    }

    @Test
    public void testAddRamping() { // TODO: flesh this out a bit more
        EventCell update = new EventCell();
        FloatOutput fo = cfo1.addRamping(0.2f, update);
        for (int i = 0; i < 5; i++) {
            cfo1.ifExpected = true;
            cfo1.valueExpected = 0;
            update.event();
            cfo1.check();
        }
        float last = 0;
        for (float i = -5f; i < 5f; i++) {
            fo.set(i);
            int j = 0;
            while (true) {
                cfo1.ifExpected = true;
                last = cfo1.valueExpected = Utils.updateRamping(last, i, 0.2f);
                update.event();
                cfo1.check();
                if (last == i) {
                    break;
                }
                if (++j >= 100) {
                    fail("never reached target");
                }
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void testAddRampingNull() {
        cfo1.addRamping(0.2f, null);
    }

    @Test
    public void testViaDerivative() throws InterruptedException {
        Random rand = new Random(1);
        FloatOutput fo = cfo1.viaDerivative();
        float j = 0;
        for (float i = -10.0f; i <= 10.0f; i += 0.5f) {
            cfo1.valueExpected = (i - j); // for subsequent times around the
            // loop
            fo.set(i);
            cfo1.check();
            for (j = i; j <= i + 20.0f;) {
                float delta = (rand.nextInt(30) + 1) / 10.0f;
                long timeDelta = rand.nextInt(2000) + 1;
                fake.forward(timeDelta);
                j += delta;
                cfo1.ifExpected = true;
                cfo1.valueExpected = 1000 * delta / timeDelta;
                cfo1.maxDelta = 0.00001f * Math.abs(cfo1.valueExpected);
                fo.set(j);
                cfo1.check();
            }
            fake.forward(1000);
            cfo1.ifExpected = true;
        }
    }

    @Test
    public void testFilter() {
        for (boolean not : new boolean[] { false, true }) {
            BooleanCell allowDeny = new BooleanCell();
            FloatCell out = new FloatCell();
            FloatOutput fo = not ? out.filterNot(allowDeny) : out.filter(allowDeny);
            int j = 0;
            float expect = 0;
            for (int i = 0; i < 10; i++) {
                for (float f : Values.interestingFloats) {
                    boolean nvalue = ((j % 19) < 10) ^ not;
                    if (allowDeny.get() == not && nvalue != not) {
                        float f2 = Values.getRandomFloat();
                        // switching to active: expect an update as necessary
                        fo.set(f2);
                        assertEquals(Float.floatToIntBits(expect), Float.floatToIntBits(out.get()));

                        allowDeny.set(nvalue);
                        expect = f2;

                        assertEquals(Float.floatToIntBits(expect), Float.floatToIntBits(out.get()));
                    } else {
                        allowDeny.set(nvalue);
                    }

                    fo.set(f);
                    if (allowDeny.get() ^ not) {
                        expect = f;
                    }
                    assertEquals(Float.floatToIntBits(expect), Float.floatToIntBits(out.get()));

                    j++;
                }
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void testFilterNull() {
        cfo1.filter(null);
    }

    @Test(expected = NullPointerException.class)
    public void testFilterNotNull() {
        cfo1.filterNot(null);
    }

    @Test
    public void testFromBoolean1() {
        FloatCell offV = new FloatCell();
        FloatCell onV = new FloatCell();
        Boolean lastSent = null;
        BooleanOutput b1 = cfo1.fromBoolean(offV, onV);
        for (float off : Values.interestingFloats) {
            cfo1.ifExpected = (lastSent != null && !lastSent);
            cfo1.valueExpected = off;
            offV.set(off);
            cfo1.check();
            for (float on : Values.interestingFloats) {
                cfo1.ifExpected = (lastSent != null && lastSent);
                cfo1.valueExpected = on;
                onV.set(on);
                cfo1.check();
                for (boolean b : Values.interestingBooleans) {
                    cfo1.valueExpected = b ? on : off;

                    cfo1.ifExpected = (lastSent == null || lastSent != b);
                    b1.set(b);
                    cfo1.check();

                    lastSent = b;
                }
            }
            boolean b = Values.getRandomBoolean();
            cfo1.ifExpected = (lastSent != null && lastSent != b);
            cfo1.valueExpected = b ? onV.get() : off;
            b1.set(b);
            cfo1.check();
            lastSent = b;
        }
    }

    @Test(expected = NullPointerException.class)
    public void testFromBoolean1NullA() {
        cfo1.fromBoolean(null, FloatInput.zero);
    }

    @Test(expected = NullPointerException.class)
    public void testFromBoolean1NullB() {
        cfo1.fromBoolean(FloatInput.zero, null);
    }

    @Test
    public void testFromBoolean2() {
        for (float off : Values.interestingFloats) {
            Boolean lastSent = null;
            FloatCell onV = new FloatCell();
            BooleanOutput b1 = cfo1.fromBoolean(off, onV);
            for (float on : Values.interestingFloats) {
                cfo1.ifExpected = (lastSent != null && lastSent);
                cfo1.valueExpected = on;
                onV.set(on);
                cfo1.check();
                for (boolean b : Values.interestingBooleans) {
                    cfo1.valueExpected = b ? on : off;

                    cfo1.ifExpected = (lastSent == null || lastSent != b);
                    b1.set(b);
                    cfo1.check();

                    lastSent = b;
                }
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void testFromBoolean2Null() {
        cfo1.fromBoolean(0, null);
    }

    @Test
    public void testFromBoolean3() {
        for (float on : Values.interestingFloats) {
            Boolean lastSent = null;
            FloatCell offV = new FloatCell();
            BooleanOutput b1 = cfo1.fromBoolean(offV, on);
            for (float off : Values.interestingFloats) {
                cfo1.ifExpected = (lastSent != null && !lastSent);
                cfo1.valueExpected = on;
                offV.set(off);
                cfo1.check();
                for (boolean b : Values.interestingBooleans) {
                    cfo1.valueExpected = b ? on : off;

                    cfo1.ifExpected = (lastSent == null || lastSent != b);
                    b1.set(b);
                    cfo1.check();

                    lastSent = b;
                }
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void testFromBoolean3Null() {
        cfo1.fromBoolean(null, 0);
    }

    @Test
    public void testFromBoolean4() {
        for (float off : Values.interestingFloats) {
            for (float on : Values.interestingFloats) {
                Boolean lastSent = null;
                BooleanOutput b1 = cfo1.fromBoolean(off, on);
                for (boolean b : Values.interestingBooleans) {
                    cfo1.valueExpected = b ? on : off;

                    cfo1.ifExpected = (lastSent == null || lastSent != b);
                    b1.set(b);
                    cfo1.check();

                    lastSent = b;
                }
            }
        }
    }

    @Test
    public void testSafeSet() {
        evil.safeSet(0);
        evil.safeSet(1);
    }

    @Test(expected = NoSuchElementException.class)
    public void testExceptionPropagationFalse() {
        evil.set(1);
    }

    @Test(expected = NoSuchElementException.class)
    public void testCombineWithError1CausesError() {
        cfo1.ifExpected = true;
        cfo1.valueExpected = 1.2f;
        cfo1.combine(evil).set(1.2f);
    }

    @Test(expected = NoSuchElementException.class)
    public void testCombineWithError2CausesError() {
        cfo1.ifExpected = true;
        cfo1.valueExpected = 1;
        evil.combine(cfo1).set(1);
    }

    @Test
    public void testCombineWithError1Succeeds() {
        cfo1.ifExpected = true;
        cfo1.valueExpected = 1;
        cfo1.combine(evil).safeSet(1);
        cfo1.check();
    }

    @Test
    public void testCombineWithError2Succeeds() {
        cfo1.ifExpected = true;
        cfo1.valueExpected = 1;
        evil.combine(cfo1).safeSet(1);
        cfo1.check();
    }

    @Test
    public void testCombineWithError3CausesError() {
        boolean errored = false;
        try {
            evil.combine(evil).set(1);
        } catch (NoSuchElementException ex) {
            errored = true;
            assertEquals(ex.getSuppressed().length, 1);
            assertTrue(ex.getSuppressed()[0] instanceof NoSuchElementException);
        }
        assertTrue(errored);
    }
}
