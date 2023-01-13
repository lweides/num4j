package num4j.impl;

import static org.junit.jupiter.api.Assertions.*;

import num4j.exceptions.IncompatibleDimensionsException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class InMemoryMatrixTest {

    @Test
    void shouldCreateMatrixFilledWithZeros() {
        InMemoryMatrix noDimensions = InMemoryMatrix.zeros();
        assertArrayEquals(new double[0], noDimensions.data());

        InMemoryMatrix oneDimension = InMemoryMatrix.zeros(1);
        assertArrayEquals(new double[1], oneDimension.data());

        InMemoryMatrix manyDimensions = InMemoryMatrix.zeros(1, 2, 3, 4, 5);
        assertArrayEquals(new double[1 * 2 * 3 * 4 * 5], manyDimensions.data());
    }

    @Test
    void shouldCreateMatrixFilledWithOnes() {
        InMemoryMatrix noDimensions = InMemoryMatrix.ones();
        assertArrayEquals(new double[0], noDimensions.data());

        InMemoryMatrix oneDimension = InMemoryMatrix.ones(1);
        assertArrayEquals(new double[] { 1, }, oneDimension.data());

        InMemoryMatrix manyDimensions = InMemoryMatrix.ones(1, 2, 3, 4, 5);
        double[] expected = new double[1 * 2 * 3 * 4 * 5];
        Arrays.fill(expected, 1);
        assertArrayEquals(expected, manyDimensions.data());
    }

    @Test
    void addingZerosToOnesShouldResultInOnes() {
        InMemoryMatrix ones = InMemoryMatrix.ones(2);
        InMemoryMatrix zeros = InMemoryMatrix.zeros(2);
        ones.add(zeros);
        assertArrayEquals(new double[] { 1, 1, }, ones.data());
    }

    @Test
    void addingOnesToZerosShouldResultInOnes() {
        InMemoryMatrix ones = InMemoryMatrix.ones(2);
        InMemoryMatrix zeros = InMemoryMatrix.zeros(2);
        zeros.add(ones);
        assertArrayEquals(new double[] { 1, 1, }, zeros.data());
    }

    @Test
    void addingInCompatibleDimensionsShouldFail() {
        InMemoryMatrix oneTimesTwo = InMemoryMatrix.zeros(1, 2);
        InMemoryMatrix twoTimesOne = InMemoryMatrix.zeros(2, 1);
        assertThrows(
            IncompatibleDimensionsException.class,
            () -> oneTimesTwo.add(twoTimesOne)
        );
    }
}