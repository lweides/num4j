package num4j.impl;

import num4j.api.Matrix;
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

    @Test
    void transpose2D() {
        Matrix<Integer> m1 = IntegerMatrix.builder()
                .row(1, 2, 3, 4)
                .row(5, 6, 7, 8)
                .row(9, 10, 11, 12)
                .build();

        Matrix<Integer> expected = IntegerMatrix.builder()
                .row(1, 5, 9)
                .row(2, 6, 10)
                .row(3, 7, 11)
                .row(4, 8, 12)
                .build();

        assertEquals(expected, m1.transpose(1, 0));
    }

    @Test
    void transpose3D() {
        Matrix<Integer> m1 = IntegerMatrix.builder()
                .row(1, 2, 3, 4)
                .row(5, 6, 7, 8)

                .row(9, 10, 11, 12)
                .row(13, 14, 15, 16)
                .build();
        m1.reshape(2, 2, 4);

        Matrix<Integer> expected = IntegerMatrix.builder()
                .row(1, 9)
                .row(2, 10)
                .row(3, 11)
                .row(4, 12)

                .row(5, 13)
                .row(6, 14)
                .row(7, 15)
                .row(8, 16)
                .build();
        expected.reshape(2, 4, 2);

        assertEquals(expected, m1.transpose(1, 2, 0));
    }

    @Test
    void matrixMultiplication2D() {
        Matrix<Integer> m1 = IntegerMatrix.builder()
                .row(1, 2)
                .row(3, 4)
                .row(5, 6)
                .row(7, 8)
                .row(9, 10)
                .row(11, 12)
                .build();

        Matrix<Integer> m2 = IntegerMatrix.builder()
                .row(1, 2, 3)
                .row(4, 5, 6)
                .build();

        Matrix<Integer> result = m1.mmul(m2);
        Matrix<Integer> expected = IntegerMatrix.builder()
                .row(9, 12, 15)
                .row(19, 26, 33)
                .row(29, 40, 51)
                .row(39, 54, 69)
                .row(49, 68, 87)
                .row(59, 82, 105)
                .build();

        assertEquals(expected, result);
    }

    @Test
    void matrixMultiplication3D() {
        Matrix<Integer> m1 = IntegerMatrix.builder()
                .row(1, 2)
                .row(3, 4)
                .row(5, 6)

                .row(7, 8)
                .row(9, 10)
                .row(11, 12)

                .row(13, 14)
                .row(15, 16)
                .row(17, 18).build();
        m1.reshape(3, 3, 2);

        Matrix<Integer> m2 = IntegerMatrix.builder()
                .row(1, 2, 3, 4)
                .row(5, 6, 7, 8)

                .row(9, 10, 11, 12)
                .row(13, 14, 15, 16)

                .row(17, 18, 19, 20)
                .row(21, 22, 23, 24).build();
        m2.reshape(3, 2, 4);

        Matrix<Integer> result =  m1.mmul(m2);
        Matrix<Integer> expected = IntegerMatrix.builder()
                .row(11, 14, 17, 20)
                .row(23, 30, 37, 44)
                .row(35, 46, 57, 68)

                .row(167, 182, 197, 212)
                .row(211, 230, 249, 268)
                .row(255, 278, 301, 324)

                .row(515, 542, 569, 596)
                .row(591, 622, 653, 684)
                .row(667, 702, 737, 772).build();
        expected.reshape(3, 3, 4);

        assertEquals(expected, result);
    }

    @Test
    void matrixMultiplication3D2() {
        Matrix<Integer> m1 = IntegerMatrix.builder()
                .row(1, 2)
                .row(3, 4)
                .row(5, 6)
                .row(7, 8)

                .row(9, 10)
                .row(11, 12)
                .row(13, 14)
                .row(15, 16)

                .row(17, 18)
                .row(19, 20)
                .row(21, 22)
                .row(23, 24)


                .row(25, 26)
                .row(27, 28)
                .row(29, 30)
                .row(31, 32)

                .row(33, 34)
                .row(35, 36)
                .row(37, 38)
                .row(39, 40)

                .row(41, 42)
                .row(43, 44)
                .row(45, 46)
                .row(47, 48)
                .build();
        m1.reshape(2, 1, 3, 4, 2);

        Matrix<Integer> m2 = IntegerMatrix.builder()
                .row(1, 2, 3)
                .row(4, 5, 6)

                .row(7, 8, 9)
                .row(10, 11, 12)

                .row(13, 14, 15)
                .row(16, 17, 18)


                .row(19, 20, 21)
                .row(22, 23, 24)

                .row(25, 26, 27)
                .row(28, 29, 30)

                .row(31, 32, 33)
                .row(34, 35, 36)


                .row(37, 38, 39)
                .row(40, 41, 42)

                .row(43, 44, 45)
                .row(46, 47, 48)

                .row(49, 50, 51)
                .row(52, 53, 54)
                .build();
        m2.reshape(3, 3, 2, 3);

        Matrix<Integer> result = m1.mmul(m2);

        Matrix<Integer> expected = IntegerMatrix.builder()
                .row(9, 12, 15)
                .row(19, 26, 33)
                .row( 29, 40, 51)
                .row(39, 54, 69)

                .row( 163,  182,  201)
                .row(197,  220,  243)
                .row(231,  258,  285)
                .row(265,  296,  327)

                .row(509,  544,  579)
                .row(567,  606,  645)
                .row(625,  668,  711)
                .row(683,  730,  777)

                .row(63,   66,   69)
                .row(145,  152,  159)
                .row(227,  238,  249)
                .row(309,  324,  339)

                .row(505,  524,  543)
                .row(611,  634,  657)
                .row(717,  744,  771)
                .row(823,  854,  885)

                .row(1139, 1174, 1209)
                .row(1269, 1308, 1347)
                .row(1399, 1442, 1485)
                .row(1529, 1576, 1623)


                .row(117,  120, 123)
                .row(271,  278,  285)
                .row(425,  436,  447)
                .row(579,  594,  609)

                .row(847,  866,  885)
                .row(1025, 1048, 1071)
                .row(1203, 1230, 1257)
                .row(1381, 1412, 1443)

                .row(1769, 1804, 1839)
                .row(1971, 2010, 2049)
                .row(2173, 2216, 2259)
                .row(2375, 2422, 2469)



                .row(129,  180,  231)
                .row(139,  194,  249)
                .row(149,  208,  267)
                .row(159,  222,  285)

                .row(571,  638,  705)
                .row(605,  676,  747)
                .row(639,  714,  789)
                .row(673,  752,  831)

                .row(1205, 1288, 1371)
                .row(1263, 1350, 1437)
                .row(1321, 1412, 1503)
                .row(1379, 1474, 1569)


                .row(1047, 1098, 1149)
                .row(1129, 1184, 1239)
                .row(1211, 1270, 1329)
                .row(1293, 1356, 1419)

                .row(1777, 1844, 1911)
                .row(1883, 1954, 2025)
                .row(1989, 2064, 2139)
                .row(2095, 2174, 2253)

                .row(2699, 2782, 2865)
                .row(2829, 2916, 3003)
                .row(2959, 3050, 3141)
                .row(3089, 3184, 3279)


                .row(1965, 2016, 2067)
                .row(2119, 2174, 2229)
                .row(2273, 2332, 2391)
                .row(2427, 2490, 2553)

                .row(2983, 3050, 3117)
                .row(3161, 3232, 3303)
                .row(3339, 3414, 3489)
                .row(3517, 3596, 3675)

                .row(4193, 4276, 4359)
                .row(4395, 4482, 4569)
                .row(4597, 4688, 4779)
                .row(4799, 4894, 4989)
                .build();
        expected.reshape(2, 3, 3, 4, 3);
        assertEquals(expected, result);
    }
}