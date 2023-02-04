package num4j.impl;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.Vector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorSpecies;
import num4j.api.Matrix;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Arrays;

public class MemoryMappedIntegerMatrix extends MemoryMappedMatrix<Integer> {

    public static MemoryMappedIntegerMatrix from(Path path) throws IOException {
        RandomAccessFile file = new RandomAccessFile(path.toFile(), "r");
        byte type = file.readByte();
        if (type != INT_TYPE) {
            throw new IllegalArgumentException("References file does not contain integer matrix");
        }
        byte nrDimensions = file.readByte();
        int[] dimensions = new int[nrDimensions];

        for (int i = 0; i < nrDimensions; i++) {
            dimensions[i] = file.readInt();
        }

        int nrElements = file.readInt();
        long sizeBytes = (long) nrElements * Integer.BYTES;
        int startData = Byte.BYTES + Byte.BYTES + (nrDimensions * Integer.BYTES) + Integer.BYTES;

        FileChannel channel = file.getChannel();
        MappedByteBuffer data = channel.map(FileChannel.MapMode.READ_ONLY, startData, sizeBytes);

        return new MemoryMappedIntegerMatrix(channel, data, dimensions);
    }

    private static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

    private MemoryMappedIntegerMatrix(FileChannel channel, ByteBuffer data, int[] dimensions) {
        super(channel, data, dimensions);
    }

    @Override
    public Matrix<Integer> mmul2D(Matrix<Integer> other) {
        //TODO
        return null;
    }

    @Override
    public Matrix<Integer> copy() {
        this.data.position(0);
        int[] dimensions = Arrays.copyOf(dimensions(), dimensions().length);
        byte[] data = new byte[size() * Integer.BYTES];
        this.data.get(data);
        return new IntegerMatrix(data, dimensions);
    }

    @Override
    public Vector<Integer> toVec(int offset, VectorMask<Integer> mask) {
        return IntVector.fromByteBuffer(SPECIES, data, offset, BYTE_ORDER, mask);
    }

    @Override
    protected void writeType(OutputStream out) throws IOException {
        out.write(MemoryMappedMatrix.INT_TYPE);
    }
}
