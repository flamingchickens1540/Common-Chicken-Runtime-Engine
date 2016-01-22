/*
 * Copyright 2015 Colby Skeggs, 2016 Alexander Mackworth
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
package ccre.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class UtilsTest {
    private void checkBytesToFloatFor(float f) {
        int ibits = Float.floatToIntBits(f);
        byte[] d = new byte[] { (byte) (ibits >> 24), (byte) (ibits >> 16), (byte) (ibits >> 8), (byte) ibits };
        assertEquals(ibits, Utils.bytesToInt(d, 0));
        assertEquals(ibits, Float.floatToIntBits(Utils.bytesToFloat(d, 0)));
    }

    @Test
    public void testSimpleBytesToFloat() {
        checkBytesToFloatFor(0);
    }

    @Test
    public void testOffsetBytesToFloat() {
        int ibits = Float.floatToIntBits(17.77f);
        // junk data at start
        byte[] d = new byte[] { 3, 3, 3, 8, 1, 2, 5, (byte) (ibits >> 24), (byte) (ibits >> 16), (byte) (ibits >> 8), (byte) ibits };
        assertEquals(ibits, Utils.bytesToInt(d, 7));
        assertEquals(ibits, Float.floatToIntBits(Utils.bytesToFloat(d, 7)));
    }

    @Test
    public void testInterestingBytesToFloat() {
        for (float f : Values.interestingFloats) {
            checkBytesToFloatFor(f);
        }
    }

    @Test
    public void testGaussianBytesToFloat() {
        Random r = new Random();
        for (int i = 0; i < 10000; i++) {
            checkBytesToFloatFor((float) (r.nextGaussian() / 10000));
            checkBytesToFloatFor((float) r.nextGaussian());
            checkBytesToFloatFor((float) (r.nextGaussian() * 10000));
        }
    }

    private int[][] bytesToIntTests = new int[][] { { 0x00, 0x00, 0x00, 0x00, 0 }, { 0xFF, 0xFF, 0xFF, 0xFF, -1 }, { 0x00, 0x00, 0x00, 0x01, 1 }, { 0x00, 0x00, 0x00, 0x0A, 10 }, { 0x00, 0x00, 0x00, 0x10, 16 }, { 0x00, 0x00, 0x00, 0x42, 66 }, { 0x00, 0x00, 0x01, 0x00, 256 }, { 0x00, 0x00, 0x01, 0x42, 322 }, { 0x00, 0x00, 0xA1, 0x42, 0xA142 }, { 0x00, 0xBC, 0xFF, 0x02, 0xBCFF02 }, { 0x72, 0xEE, 0x99, 0x31, 0x72EE9931 }, { 0xCA, 0xAC, 0xBD, 0xDB, 0xCAACBDDB } };

    @Test
    public void testBytesToIntSimple() {
        assertEquals(0x1000203, Utils.bytesToInt(new byte[] { 1, 0, 2, 3 }, 0));
    }

    @Test
    public void testBytesToInt() {
        for (int[] line : bytesToIntTests) {// MSB, B, B, LSB, EXPECTED
            byte a = (byte) line[0], b = (byte) line[1], c = (byte) line[2], d = (byte) line[3];
            assertEquals(line[4], Utils.bytesToInt(new byte[] { a, b, c, d }, 0));
        }
    }

    @Test
    public void testBytesToIntOffset() {
        for (int[] line : bytesToIntTests) {// MSB, B, B, LSB, EXPECTED
            byte a = (byte) line[0], b = (byte) line[1], c = (byte) line[2], d = (byte) line[3];
            assertEquals(line[4], Utils.bytesToInt(new byte[] { 0, 0, 0, a, b, c, d }, 3));
        }
    }

    @Test
    public void testUpdateRamping() {
        for (float base : new float[] { -1, 0, 1, 0.5f, -0.5f, 0.1f, -0.1f, 0.001f, -0.001f, 0.7f, -0.7f, 56, -56, -1.3f, 1.3f }) {
            for (float limit : new float[] { 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 1f, 2f, 2.5f, 6f, 10f }) {
                assertEquals(base, Utils.updateRamping(base, base, limit), 0);
                assertEquals(base, Utils.updateRamping(base, base, -limit), 0);
                for (float rel : new float[] { 0f, 0.01f, 0.1f, 0.2f, 0.3f, 0.5f, 1f, 2f, 30f }) {
                    if (rel >= limit) {
                        assertEquals((base + limit), Utils.updateRamping(base, base + rel, limit), 0);
                        assertEquals((base + limit), Utils.updateRamping(base, base + rel, -limit), 0);
                        assertEquals((base - limit), Utils.updateRamping(base, base - rel, limit), 0);
                        assertEquals((base - limit), Utils.updateRamping(base, base - rel, -limit), 0);
                    } else {
                        assertEquals((base + rel), Utils.updateRamping(base, base + rel, limit), 0);
                        assertEquals((base + rel), Utils.updateRamping(base, base + rel, -limit), 0);
                        assertEquals((base - rel), Utils.updateRamping(base, base - rel, limit), 0);
                        assertEquals((base - rel), Utils.updateRamping(base, base - rel, -limit), 0);
                    }
                }
            }
            for (float rel : new float[] { 0f, 0.01f, 0.1f, 0.2f, 0.3f, 0.5f, 1f, 2f, 30f }) {
                assertEquals((base + rel), Utils.updateRamping(base, base + rel, 0), 0);
            }
        }
    }

    @Test
    public void testDeadzone() {
        for (float i = -17.6f; i <= 17.6f; i += 0.1f) {
            float dz = Utils.deadzone(i, 7.2f);
            if (i >= -7.2f && i <= 7.2f) {
                assertEquals(0f, dz, 0);
            } else {
                assertEquals(i, dz, 0);
            }
        }
    }

    @Test
    public void testMethodCaller() {
        CallerInfo info = Utils.getMethodCaller(0);
        assertEquals("ccre.util.UtilsTest", info.getClassName());
        assertEquals("UtilsTest.java", info.getFileName());
        assertEquals("testMethodCaller", info.getMethodName());
    }

    @Test
    public void testMethodCallerLineNumber() {
        CallerInfo info = Utils.getMethodCaller(0);
        CallerInfo info2 = Utils.getMethodCaller(0);
        assertEquals(info2.getLineNum() - 1, info.getLineNum());
    }

    @Test
    public void testMethodCallerInvalid() {
        for (int i = -10; i < 0; i++) {
            assertNull("Got caller info for internals!", Utils.getMethodCaller(i));
        }
        assertNull("Got caller info for what should be off the end of the stack trace!", Utils.getMethodCaller(1000));
    }

    @Test
    public void testToStringThrowable() {
        CallerInfo info = Utils.getMethodCaller(0);
        String got = Utils.toStringThrowable(new Throwable("Example"));
        String[] pts = got.split("\n");
        assertNotNull(info.getFileName());
        assertNotNull(info.getMethodName());
        assertTrue(info.getLineNum() > 0);
        assertEquals("java.lang.Throwable: Example", pts[0]);
        int expectedLine = info.getLineNum() + 1;
        assertEquals("\tat " + info.getClassName() + "." + info.getMethodName() + "(" + info.getFileName() + ":" + expectedLine + ")", pts[1]);
    }

    @Test
    public void testNullToStringThrowable() {
        assertNull(Utils.toStringThrowable(null));
    }

    @Test
    public void testJoinStrings() {
        // TODO: generate lists of different lengths
        String separator = Values.getRandomString();
        List<String> pathComponents = Arrays.asList(Values.getRandomString(), Values.getRandomString(), Values.getRandomString());
        String result = Utils.joinStrings(pathComponents, separator);
        assertEquals(pathComponents.get(0) + separator + pathComponents.get(1) + separator + pathComponents.get(2), result);
    }

    @Test(expected = NullPointerException.class)
    public void testJoinStringsNullStrings() {
        Utils.joinStrings(null, ",");
    }

    @Test(expected = NullPointerException.class)
    public void testJoinStringsNullSeparator() {
        List<String> pathComponents = Arrays.asList("beginning", "middle", "end");
        Utils.joinStrings(pathComponents, null);
    }

    @Test(expected = NullPointerException.class)
    public void testJoinStringsBothArgumentsNull() {
        Utils.joinStrings(null, null);
    }

    @Test
    public void testJoinStringsEmptyArguments() {
        for (int i = 0; i < 10; i++) {
            String separator = Values.getRandomString();
            List<String> pathComponents = Arrays.asList(Values.getRandomString(), Values.getRandomString(), Values.getRandomString());
            assertEquals(Utils.joinStrings(new ArrayList<String>(), separator), "");
            assertEquals(Utils.joinStrings(pathComponents, ""), pathComponents.get(0) + pathComponents.get(1) + pathComponents.get(2));
        }
    }

    @Test(expected = NullPointerException.class)
    public void testJoinStringsAllNullElements() {
        Utils.joinStrings(Arrays.asList(null, null), ",");
    }

    @Test(expected = NullPointerException.class)
    public void testJoinStringsNullElementBeginning() {
        Utils.joinStrings(Arrays.asList(null, "middle", "end"), ",");
    }

    @Test(expected = NullPointerException.class)
    public void testJoinStringsNullElementMiddle() {
        Utils.joinStrings(Arrays.asList("beginning", null, "end"), ",");
    }

    @Test(expected = NullPointerException.class)
    public void testJoinStringsNullElementEnd() {
        Utils.joinStrings(Arrays.asList("beginning", "middle", null), ",");
    }

}
