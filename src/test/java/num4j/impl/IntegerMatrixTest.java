package num4j.impl;

import num4j.exceptions.IncompatibleDimensionsException;
import num4j.unsafe.TheUnsafe;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntegerMatrixTest {

    @Test
    void shouldCreateMatrixFilledWithZeros() {
        IntegerMatrix noDimensions = IntegerMatrix.zeros();
        assertArrayEquals(new byte[0], noDimensions.data());

        IntegerMatrix oneDimension = IntegerMatrix.zeros(1);
        assertArrayEquals(new byte[4], oneDimension.data());

        IntegerMatrix manyDimensions = IntegerMatrix.zeros(1, 2, 3, 4, 5);
        assertArrayEquals(new byte[(1 * 2 * 3 * 4 * 5) * 4], manyDimensions.data());
    }

    @Test
    void shouldCreateMatrixFilledWithOnes() {
        IntegerMatrix noDimensions = IntegerMatrix.ones();
        assertArrayEquals(new byte[0], noDimensions.data());

        IntegerMatrix oneDimension = IntegerMatrix.ones(1);
        byte[] expectedOne = new byte[4];
        TheUnsafe.write(expectedOne, 0, 1);
        assertArrayEquals(expectedOne, oneDimension.data());

        IntegerMatrix manyDimensions = IntegerMatrix.ones(1, 2, 3, 4, 5);
        byte[] expectedMany = new byte[(1 * 2 * 3 * 4 * 5) * 4];
        for (int i = 0; i < manyDimensions.size(); i++) {
            TheUnsafe.write(expectedMany, i, 1);
        }
        assertArrayEquals(expectedMany, manyDimensions.data());
    }

    @Test
    void addingZerosToOnesShouldResultInOnes() {
        IntegerMatrix ones = IntegerMatrix.ones(2);
        IntegerMatrix zeros = IntegerMatrix.zeros(2);
        ones.add(zeros);

        byte[] expected = new byte[8];
        TheUnsafe.write(expected, 0, 1);
        TheUnsafe.write(expected, 1, 1);

        assertArrayEquals(expected, ones.data());
    }

    @Test
    void addingOnesToZerosShouldResultInOnes() {
        IntegerMatrix zeros = IntegerMatrix.zeros(2);
        IntegerMatrix ones = IntegerMatrix.ones(2);
        zeros.add(ones);

        byte[] expected = new byte[8];
        TheUnsafe.write(expected, 0, 1);
        TheUnsafe.write(expected, 1, 1);

        assertArrayEquals(expected, zeros.data());
    }

    @Test
    void addingOnesToOnesShouldResultInTwos() {
        IntegerMatrix a = IntegerMatrix.ones(2);
        IntegerMatrix b = IntegerMatrix.ones(2);
        a.add(b);

        byte[] expected = new byte[8];
        TheUnsafe.write(expected, 0, 2);
        TheUnsafe.write(expected, 1, 2);

        assertArrayEquals(expected, a.data());
    }

    @Test
    void addingInCompatibleDimensionsShouldFail() {
        IntegerMatrix oneTimesTwo = IntegerMatrix.zeros(1, 2);
        IntegerMatrix twoTimesOne = IntegerMatrix.zeros(2, 1);
        assertThrows(
            IncompatibleDimensionsException.class,
            () -> oneTimesTwo.add(twoTimesOne)
        );
    }
}