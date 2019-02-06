package contents.math;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteMath {

    // Turns an unsigned byte to an int, since the "(int)byte" typecast in Java is signed
    public static int unsignedByteToInt(byte b) {
        return b & 0xFF;
    }

    // Returns the int made out of 4 bytes, taken from "bytes" starting at "offset"
    // Uses Big Endian
    public static int bytesToInt(byte[] bytes) throws ArrayIndexOutOfBoundsException{
        return bytesToInt(bytes, 0);
    }

    public static int bytesToInt(byte[] bytes, int offset) throws ArrayIndexOutOfBoundsException{

        if(offset + 3 >= bytes.length){
            throw new ArrayIndexOutOfBoundsException("Attempted to read an int past the array");
        }

        return ByteBuffer.allocate(4).put(bytes, offset, 4).getInt(0);

        // return bytes[offset] << 24 | (bytes[offset+1] & 0xff) << 16 | (bytes[offset+2] & 0xff) << 8 | (bytes[offset+3] & 0xff);
    }


    public static byte[] intToBytes(int i){
        return ByteBuffer.allocate(4).putInt(i).array();
    }


    // Thanks to Dave L on StackOverflow
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        if (len % 2 != 0){
            throw new IllegalArgumentException("Hex string of odd length.");
        }

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String byteArrayToHexString(byte[] bytes){
        char[] hexed = new char[bytes.length * 2];
        byte byteBuffer;

        for(int i = 0; i < bytes.length; i++){
            hexed[i*2] = hexArray[((byte)(bytes[i] >>> 4) & 0x0F)];

            byteBuffer = (byte)(bytes[i] & 0x0F);
            hexed[i*2+1] = hexArray[byteBuffer];
        }

        return new String(hexed);
    }

    // The cursed byte -> String ASCII conversion. One of the most basic things, that Java requires me to bodge in for some reason.
    public static String byteToString_UTF8(byte b){
        byte[] bub = new byte[]{b};
        return new String(bub, StandardCharsets.UTF_8);
    }

    public static boolean unsignedByteGreaterThan(byte b1, byte b2){
        return (b1 & 0xFF)>(b2 & 0xFF);
    }

    public static byte[] byteArrayAddInt(byte[] ba, int i){
        return intToBytes(bytesToInt(ba) + i);
    }
}
