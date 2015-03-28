/*
 * #%L
 * How to Lock a File
 * %%
 * Copyright (C) 2012 - 2015 Java Creed
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.javacreed.examples.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Example {
  public static void main(final String[] args) throws Exception {
    // Read from file
    final File inputFile = new File("src/main/resources/example.txt");
    try (final RandomAccessFile raf = new RandomAccessFile(inputFile, "rw")) {
      final FileChannel fc = raf.getChannel();
      final FileLock fl = fc.tryLock();
      if (fl == null) {
        // Failed to acquire lock
      } else {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final WritableByteChannel outChannel = Channels.newChannel(baos)) {
          for (final ByteBuffer buffer = ByteBuffer.allocate(1024); fc.read(buffer) != -1;) {
            buffer.flip();
            outChannel.write(buffer);
            buffer.clear();
          }

          Files.write(Paths.get("target", "example.txt"), baos.toByteArray());
        } finally {
          fl.release();
        }
      }
    }
  }
}
