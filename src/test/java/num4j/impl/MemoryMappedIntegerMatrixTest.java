package num4j.impl;

import num4j.api.Matrix;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.*;

class MemoryMappedIntegerMatrixTest {

    @Test
    void shouldWriteMatrix() throws IOException {
        Matrix<Integer> outMatrix = IntegerMatrix.builder()
            .row(1, 2, 3)
            .row(4, 5, 6)
            .build();

        // note that this test creates a file, but does not delete it - we failed to delete the file :shrugging:
        Path matrix = Path.of("foo");
        OutputStream outputStream = Files.newOutputStream(matrix, StandardOpenOption.CREATE);
        outMatrix.write(outputStream);
        outputStream.close();

        try (MemoryMappedIntegerMatrix inMatrix = MemoryMappedIntegerMatrix.from(matrix)) {
            Matrix<Integer> foo = inMatrix.copy();
            assertEquals(outMatrix, foo);
        }
    }
}