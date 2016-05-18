Locking a file is quite simple in Java.  The <code>FileChannel</code> (<a href="http://docs.oracle.com/javase/7/docs/api/java/nio/channels/FileChannel.html" target="_blank">Java Doc</a>) provides all methods we need to lock files.  The <code>tryLock()</code> (<a href="http://docs.oracle.com/javase/7/docs/api/java/nio/channels/FileChannel.html#tryLock()" target="_blank">Java Doc</a>) method will try to obtain the lock on the file without waiting.  If the lock is acquired an instance of <code>FileLock</code> (<a href="http://docs.oracle.com/javase/7/docs/api/java/nio/channels/FileLock.html" target="_blank">Java Doc</a>) is returned, otherwise this method returns <code>null</code>.


The following example shows how to obtain a file lock.


<pre>
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

public class Example {
  public static void main(final String[] args) throws Exception {
    <span class="comment">// Read from file</span>
    final File inputFile = new File("src/main/resources/example.txt");
    try (final RandomAccessFile raf = new RandomAccessFile(inputFile, "rw")) {
      final FileChannel fc = raf.getChannel();
      final FileLock fl = fc.tryLock();
      if (fl == null) {
        <span class="comment">// Failed to acquire lock</span>
      } else {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final WritableByteChannel outChannel = Channels.newChannel(baos)) {
          for (final ByteBuffer buffer = ByteBuffer.allocate(1024); fc.read(buffer) != -1;) {
            buffer.flip();
            outChannel.write(buffer);
            buffer.clear();
          }

          Files.write(new File("target/example.txt").toPath(), baos.toByteArray());
        } finally {
          fl.release();
        }
      }
    }
  }
}
</pre>
