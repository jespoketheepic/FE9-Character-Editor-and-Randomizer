package contents.io;

import java.io.*;
import java.util.Arrays;

public class InputStreamTools {
    // Loop that reads from InputStream into the buffer until a null
    public static byte[] readUntilNull(BufferedInputStream inputStream) throws IOException {
        byte zero = 0x00;
        return readUntilSeparator(inputStream, zero);
    }

    // Loop that reads from InputStream into the buffer until a separator character
    // Optimized for <100 byte reads, make an alternative when bigger is needed
    @SuppressWarnings("Duplicates")
    public static byte[] readUntilSeparator(InputStream inputStream, byte separator) throws IOException {
        int safety;
        int buffersize = 100;
        byte[] buffer = new byte[buffersize];

        for (int i = 0; ; i++) {
            if ((safety = inputStream.read()) == -1) {
                throw new EOFException("File ran out!");
            } else {
                // If the buffer is too small somehow
                if (i >= buffersize) {
                    buffer = Arrays.copyOf(buffer, i+10);
                    buffersize += 10;
                }
                buffer[i] = (byte)safety;
                if (buffer[i] == separator) {
                    return Arrays.copyOf(buffer, i);
                }
            }
        }
    }

    // Same as above, but for RandomAccessFile, since they don't share a reading interface
    @SuppressWarnings("Duplicates")
    public static byte[] readUntilSeparator(RandomAccessFile randomAccessFile, byte separator) throws IOException {
        int safety;
        int buffersize = 100;
        byte[] buffer = new byte[buffersize];

        for (int i = 0; ; i++) {
            if ((safety = randomAccessFile.read()) == -1) {
                throw new EOFException("File ran out!");
            } else {
                // If the buffer is too small somehow
                if (i >= buffersize) {
                    buffer = Arrays.copyOf(buffer, i+10);
                    buffersize += 10;
                }
                buffer[i] = (byte)safety;
                if (buffer[i] == separator) {
                    return Arrays.copyOf(buffer, i);
                }
            }
        }
    }

    // Reads 4 bytes from the given InputStream into the destination byte array
    public static void read4Bytes(BufferedInputStream inputStream, byte[] dest) throws IOException, IllegalArgumentException{
        if (dest.length < 4){
            throw new IllegalArgumentException("Pointer array too small for a pointer?");
        }
        int safety;
        for (int i = 0; i < 4; i++) {
            if ((safety = inputStream.read()) == -1) {
                throw new EOFException("Pointer file ran out!");
            } else {
                dest[i] = (byte)safety;
            }
        }
    }

    public static byte[] read4Bytes(BufferedInputStream inputStream) throws IOException{
        byte[] fourBytes = new byte[4];
        read4Bytes(inputStream, fourBytes);
        return fourBytes;
    }

}
