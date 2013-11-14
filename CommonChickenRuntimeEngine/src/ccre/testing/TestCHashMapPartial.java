/*
 * Copyright 2013 Colby Skeggs
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
package ccre.testing;

import ccre.util.CHashMap;
import java.util.Random;

/**
 * A test that tests some parts of the CHashMap class.
 *
 * @author skeggsc
 */
public class TestCHashMapPartial extends BaseTest {

    @Override
    public String getName() {
        return "CHashMap minimal test";
    }

    @Override
    protected void runTest() throws TestingException {
        String[] randoms = {"abcd", "aoeu", "pickles", "overstatement", "understudy", "arrays", "backwards", "sdrawkcab", "additional", "foo", "bar", "1540", "413", "612", "1025", "1111", "yes that is", "many-sided-pickle-adventurer", "chickens", "ignited", "pi", "are you even reading these by now?", "long string!!!!!!......//////_______ hahahahah", "I hope that this works.", "I could do brainstorming in here.", "Finishing up...", "green", "violet", "magenta", "purple", "indigo", "cerulean", "cobalt", "cheese", "potatos", "nitrates", "sodium", "carbohydrates", "snakes", "office", "pocketbook"};
        String[] keys = new String[10];
        String[] values = new String[keys.length];
        Random r = new Random();
        // TODO: Make this nicer?
        for (int i = 0; i < keys.length; i++) {
            String want;
            do {
                want = randoms[r.nextInt(randoms.length)];
                for (int j = 0; j < i; j++) {
                    if (want.equals(keys[j])) {
                        want = null;
                        break;
                    }
                }
            } while (want == null);
            keys[i] = want;
            values[i] = randoms[r.nextInt(randoms.length)];
        }
        CHashMap<String, String> testee = new CHashMap<String, String>();
        for (int i = 0; i < keys.length; i++) {
            testee.put(keys[i], values[i]);
        }
        for (int i = 0; i < keys.length; i++) {
            assertIEqual(testee.get(keys[i]), values[i], "HashMap returned wrong value!");
        }
        boolean[] found = new boolean[keys.length];
        for (String s : testee) {
            for (int i = 0; i < keys.length; i++) {
                if (keys[i] == s) {
                    assertFalse(found[i], "Already found that key!");
                    found[i] = true;
                }
            }
        }
        for (boolean b : found) {
            assertTrue(b, "Did not complete all keys!");
        }
    }
}
