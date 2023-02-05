package num4j.impl;

import num4j.api.Matrix;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public abstract class MemoryMappedMatrix<T extends Number> implements Matrix<T>, AutoCloseable {

    protected static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    protected static final byte INT_TYPE = 0x0;
    protected static final byte DOUBLE_TYPE = 0x1;

    private final FileChannel channel;
    protected final ByteBuffer data;
    private final int[] dimensions;

    public MemoryMappedMatrix(FileChannel channel, ByteBuffer data, int[] dimensions) {
        this.channel = channel;
        this.data = data;
        this.dimensions = dimensions;
    }

    @Override
    public void add(Matrix<T> other) {
        throw unmodifiable();
    }

    @Override
    public void sub(Matrix<T> other) {
        throw unmodifiable();
    }

    @Override
    public void mul(Matrix<T> other) {
        throw unmodifiable();
    }

    @Override
    public void div(Matrix<T> other) {
        throw unmodifiable();
    }

    @Override
    public Matrix<T> transpose(int... swap) {
        return copy().transpose(swap);
    }

    @Override
    public Matrix<T> mmul(Matrix<T> other) {
        throw unmodifiable();
    }

    @Override
    public void reshape(int[] dimensions) {
        throw unmodifiable();
    }

    @Override
    public void set(T value, int... position) {
        throw unmodifiable();
    }

    @Override
    public int[] dimensions() {
        return dimensions;
    }

    @Override
    public int size() {
        return Arrays.stream(dimensions)
            .reduce((i1, i2) -> i1 * i2)
            .orElse(0);
    }

    @Override
    public byte[] data() {
        throw unmodifiable();
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

    @Override
    public void write(OutputStream out) throws IOException {
        writeType(out);
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeByte(dimensions.length);
        for (int dim : dimensions) {
            dataOutputStream.writeInt(dim);
        }
        dataOutputStream.writeInt(size());
        data.position(0);
        Channels.newChannel(out).write(data);
    }

    protected abstract void writeType(OutputStream out) throws IOException;

    private UnsupportedOperationException unmodifiable() {
        return new UnsupportedOperationException("Matrix cannot be modified");
    }
}
