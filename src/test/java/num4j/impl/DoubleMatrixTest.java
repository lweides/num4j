package num4j.impl;

import static org.junit.jupiter.api.Assertions.*;

import num4j.api.Matrix;
import num4j.exceptions.IncompatibleDimensionsException;
import num4j.unsafe.TheUnsafe;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.stream.IntStream;

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

    @Test
    void sumShouldWork() {
        Matrix<Double> a = DoubleMatrix.builder()
            .row(1.0, 1.0, 2.0)
            .row(2.0, 3.0, 4.0)
            .build();

        Matrix<Double> b = DoubleMatrix.builder()
            .row(2.0, 3.0, 4.0)
            .row(1.0, 6.0, 7.2)
            .build();

        Matrix<Double> sum = a.copy();
        sum.add(b);

        Matrix<Double> expectedSum = DoubleMatrix.builder()
            .row(3.0, 4.0, 6.0)
            .row(3.0, 9.0, 11.2)
            .build();

        assertEquals(expectedSum, sum);
    }

    @Test
    void subShouldWork() {
        Matrix<Double> a = DoubleMatrix.builder()
            .row(1.0, 1.0, 2.0)
            .row(2.0, 3.0, 4.0)
            .build();

        Matrix<Double> b = DoubleMatrix.builder()
            .row(2.0, 3.0, 4.0)
            .row(1.0, 6.0, 7.2)
            .build();

        Matrix<Double> sub = a.copy();
        sub.sub(b);

        Matrix<Double> expectedSub = DoubleMatrix.builder()
            .row(-1.0, -2.0, -2.0)
            .row(1.0, -3.0, -3.2)
            .build();

        assertEquals(expectedSub, sub);
    }

    @Test
    void mulShouldWork() {
        Matrix<Double> a = DoubleMatrix.builder()
            .row(1.0, 1.0, 2.0)
            .row(2.0, 3.0, 4.0)
            .build();

        Matrix<Double> b = DoubleMatrix.builder()
            .row(2.0, 3.0, 4.0)
            .row(1.0, 6.0, 7.2)
            .build();

        Matrix<Double> mul = a.copy();
        mul.mul(b);

        Matrix<Double> expectedMul = DoubleMatrix.builder()
            .row(2.0, 3.0, 8.0)
            .row(2.0, 18.0, 28.8)
            .build();

        assertEquals(expectedMul, mul);
    }

    @Test
    void divShouldWork() {
        Matrix<Double> a = DoubleMatrix.builder()
            .row(1.0, 1.0, 2.0)
            .row(2.0, 3.0, 4.0)
            .build();

        Matrix<Double> b = DoubleMatrix.builder()
            .row(2.0, 3.0, 4.0)
            .row(1.0, 6.0, 7.2)
            .build();

        Matrix<Double> div = a.copy();
        div.div(b);

        Matrix<Double> expectedDiv = DoubleMatrix.builder()
            .row(0.5, 1.0 / 3.0, 0.5)
            .row(2.0, 0.5, 4.0 / 7.2)
            .build();

        assertEquals(expectedDiv, div);
    }

    @Test
    void transpose3D() {
        double[] small3DMatrix = IntStream.rangeClosed(1, 16).mapToDouble(i -> i).toArray();
        ByteBuffer byteBuffer = ByteBuffer.allocate(small3DMatrix.length * 8);
        DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
        doubleBuffer.put(small3DMatrix);

        DoubleMatrix matrix = new DoubleMatrix(byteBuffer.array(), 2,2,4);
        Matrix<Double> transposed = matrix.transpose(1,2,0);
        // original 0 1 2
        double[] expected = new double[] {
                1, 9,
                2, 10,
                3, 11,
                4, 12,

                5, 13,
                6, 14,
                7, 15,
                8, 16
        };

        doubleBuffer.clear();
        doubleBuffer.put(expected);
        assertArrayEquals(byteBuffer.array(), transposed.data());
    }

    @Test
    void matrixMultiplication2D() {
        Matrix<Double> m1 = DoubleMatrix.builder()
                .row(1.0, 2.0)
                .row(3.0, 4.0)
                .row(5.0, 6.0)
                .row(7.0, 8.0)
                .row(9.0, 10.0)
                .row(11.0, 12.0)
                .build();

        Matrix<Double> m2 = DoubleMatrix.builder()
                .row(1.0, 2.0, 3.0)
                .row(4.0, 5.0, 6.0)
                .build();

        Matrix<Double> result = m1.mmul(m2);

        Matrix<Double> expected = DoubleMatrix.builder()
                .row(9.0, 12.0, 15.0)
                .row(19.0, 26.0, 33.0)
                .row(29.0, 40.0, 51.0)
                .row(39.0, 54.0, 69.0)
                .row(49.0, 68.0, 87.0)
                .row(59.0, 82.0, 105.0)
                .build();

        assertEquals(expected, result);
    }
}