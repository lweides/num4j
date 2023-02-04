package num4j.impl;

import num4j.api.Matrix;
import num4j.exceptions.IncompatibleDimensionsException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DoubleMatrixBuilderTest {

    @Test
    void shouldCreateMatrixOfCorrectDimensions() {
        Matrix<Double> matrix = DoubleMatrix.builder()
            .row(1.0, 2.0, 3.0)
            .row(4.0, 5.0, 6.0)
            .build();

        assertArrayEquals(new int[] { 2, 3 }, matrix.dimensions());
    }

    @Test
    void specifyingNonMatchingSubsequentRowsShouldThrow() {
        assertThrows(
            IncompatibleDimensionsException.class,
            () -> DoubleMatrix.builder()
                    .row(1.0, 2.0)
                    .row(3.0)
        );
    }

    @Test
    void addingNoRowsIsInvalid() {
        assertThrows(
            IllegalStateException.class,
            () -> DoubleMatrix.builder().build()
        );
    }
}
