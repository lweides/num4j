package num4j.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class TheUnsafe {
    private static final Unsafe UNSAFE;
    private static final int BYTE_ARRAY_OFFSET;
    private static final int BYTE_ARRAY_SCALE;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
            BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
            BYTE_ARRAY_SCALE = UNSAFE.arrayIndexScale(byte[].class);
        } catch (Exception ex) { throw new Error(ex); }
    }

    public static void write(byte[] data, long offset, int value) {
        long offsetInByte = BYTE_ARRAY_OFFSET + offset * BYTE_ARRAY_SCALE * Integer.BYTES;
        UNSAFE.putInt(data, offsetInByte, value);
    }

    public static void write(byte[] data, long offset, double value) {
        long offsetInByte = BYTE_ARRAY_OFFSET + offset * BYTE_ARRAY_SCALE * Double.BYTES;
        UNSAFE.putDouble(data, offsetInByte, value);
    }
}
