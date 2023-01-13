package num4j.unsafe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TheUnsafeTest {

    @Test
    void shouldWriteIntegerIntoByteArray() {
        byte[] data = new byte[4];
        TheUnsafe.write(data, 0, 1);
        assertEquals(1, data[0]);
        assertEquals(0, data[1]);
        assertEquals(0, data[2]);
        assertEquals(0, data[3]);

        TheUnsafe.write(data, 0, 1 << 8);
        assertEquals(0, data[0]);
        assertEquals(1, data[1]);
        assertEquals(0, data[2]);
        assertEquals(0, data[3]);

        TheUnsafe.write(data, 0, 1 << 16);
        assertEquals(0, data[0]);
        assertEquals(0, data[1]);
        assertEquals(1, data[2]);
        assertEquals(0, data[3]);

        TheUnsafe.write(data, 0, 1 << 24);
        assertEquals(0, data[0]);
        assertEquals(0, data[1]);
        assertEquals(0, data[2]);
        assertEquals(1, data[3]);
    }

    @Test
    void shouldWriteAtOffset() {
        byte[] data = new byte[8];
        TheUnsafe.write(data, 1, 1);
        assertEquals(0, data[0]);
        assertEquals(0, data[1]);
        assertEquals(0, data[2]);
        assertEquals(0, data[3]);
        assertEquals(1, data[4]);
        assertEquals(0, data[5]);
        assertEquals(0, data[6]);
        assertEquals(0, data[7]);
    }

    @Test
    void shouldWriteIntSpanningMultipleBytes() {
        byte[] data = new byte[4];
        TheUnsafe.write(data, 0, 1 | 2 << 8 | 3 << 16 | 4 << 24);
        assertEquals(1, data[0]);
        assertEquals(2, data[1]);
        assertEquals(3, data[2]);
        assertEquals(4, data[3]);
    }
}