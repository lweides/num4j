package num4j.impl;

import num4j.api.Matrix;
import num4j.exceptions.IncompatibleDimensionsException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntegerMatrixBuilderTest {

    @Test
    void shouldCreateMatrixOfCorrectDimensions() {
        Matrix<Integer> matrix = IntegerMatrix.builder()
            .row(1, 2, 3)
            .row(4, 5, 6)
            .build();

        assertArrayEquals(new int[] { 2, 3 }, matrix.dimensions());
    }

    @Test
    void specifyingNonMatchingSubsequentRowsShouldThrow() {
        assertThrows(
            IncompatibleDimensionsException.class,
            () -> IntegerMatrix.builder()
                .row(1, 2)
                .row(3)
        );
    }

    @Test
    void addingNoRowsIsInvalid() {
        assertThrows(
            IllegalStateException.class,
            () -> IntegerMatrix.builder().build()
        );
    }
}
