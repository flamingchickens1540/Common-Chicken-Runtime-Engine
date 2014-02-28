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
package ccre.saver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A fake storage provider that works, except that it doesn't save or load
 * anything to disk. This is used if a proper provider cannot be found.
 *
 * @author skeggsc
 */
class FakeStorageProvider extends StorageProvider {

    @Override
    protected StorageSegment open(String name) {
        return new HashMappedStorageSegment() {
            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };
    }

    @Override
    protected OutputStream openOutputFile(String name) throws IOException {
        throw new IOException("Cannot write to any files in a FakeStorageProvider!");
    }

    @Override
    protected InputStream openInputFile(String name) throws IOException {
        return null;
    }
}
