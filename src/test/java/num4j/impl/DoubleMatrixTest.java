package num4j.impl;

import static org.junit.jupiter.api.Assertions.*;

import num4j.exceptions.IncompatibleDimensionsException;
import num4j.unsafe.TheUnsafe;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class DoubleMatrixTest {

    @Test
    void shouldCreateMatrixFilledWithZeros() {
        DoubleMatrix noDimensions = DoubleMatrix.zeros();
        assertArrayEquals(new byte[0], noDimensions.data());

        DoubleMatrix oneDimension = DoubleMatrix.zeros(1);
        assertArrayEquals(new byte[8], oneDimension.data());

        DoubleMatrix manyDimensions = DoubleMatrix.zeros(1, 2, 3, 4, 5);
        assertArrayEquals(new byte[(1 * 2 * 3 * 4 * 5) * 8], manyDimensions.data());
    }

    @Test
    void shouldCreateMatrixFilledWithOnes() {
        DoubleMatrix noDimensions = DoubleMatrix.ones();
        assertArrayEquals(new byte[0], noDimensions.data());

        DoubleMatrix oneDimension = DoubleMatrix.ones(1);
        byte[] expectedOne = new byte[8];
        TheUnsafe.write(expectedOne, 0, 1.0);
        assertArrayEquals(expectedOne, oneDimension.data());

        DoubleMatrix manyDimensions = DoubleMatrix.ones(1, 2, 3, 4, 5);
        byte[] expectedMany = new byte[(1 * 2 * 3 * 4 * 5) * 8];
        for (int i = 0; i < manyDimensions.size(); i++) {
            TheUnsafe.write(expectedMany, i, 1.0);
        }
        assertArrayEquals(expectedMany, manyDimensions.data());
    }

    @Test
    void addingZerosToOnesShouldResultInOnes() {
        DoubleMatrix ones = DoubleMatrix.ones(2);
        DoubleMatrix zeros = DoubleMatrix.zeros(2);
        ones.add(zeros);

        byte[] expected = new byte[16];
        TheUnsafe.write(expected, 0, 1.0);
        TheUnsafe.write(expected, 1, 1.0);

        assertArrayEquals(expected, ones.data());
    }

    @Test
    void addingOnesToZerosShouldResultInOnes() {
        DoubleMatrix zeros = DoubleMatrix.zeros(2);
        DoubleMatrix ones = DoubleMatrix.ones(2);
        zeros.add(ones);

        byte[] expected = new byte[16];
        TheUnsafe.write(expected, 0, 1.0);
        TheUnsafe.write(expected, 1, 1.0);

        assertArrayEquals(expected, zeros.data());
    }

    @Test
    void addingOnesToOnesShouldResultInTwos() {
        DoubleMatrix a = DoubleMatrix.ones(2);
        DoubleMatrix b = DoubleMatrix.ones(2);
        a.add(b);

        byte[] expected = new byte[16];
        TheUnsafe.write(expected, 0, 2.0);
        TheUnsafe.write(expected, 1, 2.0);

        assertArrayEquals(expected, a.data());
    }

    @Test
    void addingInCompatibleDimensionsShouldFail() {
        DoubleMatrix oneTimesTwo = DoubleMatrix.zeros(1, 2);
        DoubleMatrix twoTimesOne = DoubleMatrix.zeros(2, 1);
        assertThrows(
            IncompatibleDimensionsException.class,
            () -> oneTimesTwo.add(twoTimesOne)
        );
    }
}