package num4j.impl;

import jdk.incubator.vector.Vector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import num4j.api.Builder;
import num4j.api.Matrix;
import num4j.exceptions.IncompatibleDimensionsException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.IntStream;

abstract class InMemoryMatrix<T extends Number> implements Matrix<T> {

    protected static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    /**
     * @return number of elements a matrix with the specified {@code dimensions} contains.
     */
    protected static int nrElements(int ... dimensions) {
        return Arrays.stream(dimensions)
            .reduce((i1, i2) -> i1 * i2)
            .orElse(0);
    }

    private static void ensureValidDimensions(int elementSize, byte[] data, int ... dimensions) {
        int nrElements = Arrays.stream(dimensions)
            .reduce((i1, i2) -> i1 * i2)
            .orElse(0);

        if (data.length != nrElements * (elementSize / 8)) {
            throw new IncompatibleDimensionsException("Dimensions do not fit in data");
        }
    }

    private final VectorSpecies<T> species;
    private final byte[] data;
    private int[] dimensions;

    InMemoryMatrix(VectorSpecies<T> species, byte[] data, int ... dimensions) {
        ensureValidDimensions(species.elementSize(), data, dimensions);
        this.species = species;
        this.data = data;
        this.dimensions = dimensions;
    }

    @Override
    public void add(Matrix<T> other) {
        ensureSameDimensions(other);
        int upperBound = data.length / elementSize();

        for (int offset = 0; offset < upperBound; offset += species.length()) {
            VectorMask<T> mask = species.indexInRange(offset, upperBound);
            Vector<T> va = toVec(elementSize() * offset, mask);
            Vector<T> vo = other.toVec(elementSize() * offset, mask);

            va = va.add(vo);
            va.intoByteArray(data, elementSize() * offset, BYTE_ORDER, mask);
        }
    }

    @Override
    public void sub(Matrix<T> other) {
        ensureSameDimensions(other);
        int upperBound = data.length / elementSize();

        for (int offset = 0; offset < upperBound; offset += species.length()) {
            VectorMask<T> mask = species.indexInRange(offset, upperBound);
            Vector<T> va = toVec(elementSize() * offset, mask);
            Vector<T> vo = other.toVec(elementSize() * offset, mask);

            va = va.sub(vo);
            va.intoByteArray(data, elementSize() * offset, BYTE_ORDER, mask);
        }
    }

    @Override
    public void mul(Matrix<T> other) {
        ensureSameDimensions(other);
        int upperBound = data.length / elementSize();

        for (int offset = 0; offset < upperBound; offset += species.length()) {
            VectorMask<T> mask = species.indexInRange(offset, upperBound);
            Vector<T> va = toVec(elementSize() * offset, mask);
            Vector<T> vo = other.toVec(elementSize() * offset, mask);

            va = va.mul(vo);
            va.intoByteArray(data, elementSize() * offset, BYTE_ORDER, mask);
        }
    }

    @Override
    public void div(Matrix<T> other) {
        ensureSameDimensions(other);
        int upperBound = data.length / elementSize();

        for (int offset = 0; offset < upperBound; offset += species.length()) {
            VectorMask<T> mask = species.indexInRange(offset, upperBound);
            Vector<T> va = toVec(elementSize() * offset, mask);
            Vector<T> vo = other.toVec(elementSize() * offset, mask);

            va = va.div(vo);
            va.intoByteArray(data, elementSize() * offset, BYTE_ORDER, mask);
        }
    }

    private int[] padMatrix(int[] dimensions, int offset) {
        int[] paddedMatrix = new int[dimensions.length + offset];
        Arrays.fill(paddedMatrix, 1);
        System.arraycopy(dimensions, 0, paddedMatrix, offset, dimensions.length);
        return paddedMatrix;
    }

    public Matrix<T> mmul(Matrix<T> other) {
        if (dimensions().length < 2 || other.dimensions().length < 2) {
            throw new IllegalArgumentException("Require at least 2D Matrices");
        }

        // before checking the dimensions: add dimension-padding to the smaller matrix
        if (dimensions.length > other.dimensions().length) {
            other.setDimensions(padMatrix(other.dimensions(), dimensions.length - other.dimensions().length));
        } else if (dimensions.length < other.dimensions().length) {
            setDimensions(padMatrix(dimensions, other.dimensions().length - dimensions.length));
        } // else equal -> do nothing

        // check if n-dimensions except 2D is compatible
        int[] resultDimensions = new int[dimensions.length];

        for (int i = 0; i < dimensions.length - 2; i++) {
            int d1 = dimensions[i];
            int d2 = other.dimensions()[i];
            if (d1 != d2 && d1 != 1 && d2 != 1) {
                throw new IncompatibleDimensionsException("Matrices cannot be multiplied");
            } else {
                resultDimensions[i] = Math.max(d1, d2);     //2 cases: case_1: d1 == d2 -> take either or case_2: d1 || d2 == 1 => take max
            }
        }

        // check if 2 - dimension is compatible
        if (dimensions()[dimensions.length-1] != other.dimensions()[other.dimensions().length-2]) {
            throw new IncompatibleDimensionsException("Matrices cannot be multiplied: Invalid rows/cols");
        }

        resultDimensions[dimensions.length - 2] = dimensions[dimensions.length - 2];
        resultDimensions[dimensions.length - 1] = other.dimensions()[dimensions.length - 1];

        // TODO make clone of other -> else in place transpose will change other
        int[] swap = IntStream.range(0, other.dimensions().length).toArray();
        swap[other.dimensions().length-1] = other.dimensions().length-2;
        swap[other.dimensions().length-2] = other.dimensions().length-1;

        Matrix<T> result = createEmptyMatrix(resultDimensions);
        mmul(other.transpose(swap), result, dimensions.length-1, 0, 0, 0);
        return result;
    }


    // NOTE: level+1 == current dimension. f.e.: level = 1 => 2D Matrix
    private void mmul(Matrix<T> other, Matrix<T> result, int level, int thisStart, int otherStart, int offset) {
        if (level == 1) {

            int m = dimensions()[dimensions.length - 2];
            int p = other.dimensions()[other.dimensions().length-2];    //TODO maye revert to length-1
            int n = dimensions()[dimensions.length - 1];

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < p; j++) {
                    T sum = getDefaultValue();
                    for (int k = 0; k < n; k+= species.length()) {
                        VectorMask<T> mask = species.indexInRange(k, n);
                        Vector<T> va = toVec(elementSize() * (n * i +  k + thisStart) , mask);
                        Vector<T> vb = other.toVec(elementSize() * (n * j + k + otherStart), mask);

                        va = va.mul(vb);
                        sum = add(sum,reduceLanes(va, VectorOperators.ADD));
                    }

                    int baseAddress = addressToIndex(new int[]{i, j}, Arrays.copyOfRange(result.dimensions(), dimensions.length-2, dimensions.length), 0);
                    int[] toIDx = indexToAddress(result.dimensions(),baseAddress + offset);
                    result.set(sum, toIDx);
                }
            }


        } else {
            int d1 = dimensions[dimensions.length-1 - level];
            int d2 = other.dimensions()[other.dimensions().length-1 - level];

            int destNrElements = nrElements(Arrays.copyOfRange(result.dimensions(), result.dimensions().length - level, result.dimensions().length));

            if (d1 == 1) {
                for (int i = 0; i < d2; i++) {
                    int e2 = Arrays.stream(other.dimensions()).limit(other.dimensions().length - level).reduce(1, (a, b) -> a * b);
                    mmul(other, result, level-1, thisStart, otherStart + i * other.size()/e2, offset + i * destNrElements);
                }
            } else if (d2 == 1) {
                int e1 = Arrays.stream(dimensions).limit(dimensions.length - level).reduce(1, (a, b) -> a * b);
                for (int i = 0; i < d1; i++) {
                    mmul(other, result, level-1, thisStart + i * size()/e1, otherStart, offset + i * destNrElements);
                }
            } else {
                int e1 = nrElements(Arrays.copyOfRange(dimensions, 0, dimensions.length - level));
                int e2 = nrElements(Arrays.copyOfRange(other.dimensions(), 0, other.dimensions().length - level));
                for (int i = 0; i < d1; i++) {
                    mmul(other, result, level-1, thisStart + i * size()/e1, otherStart + i * other.size()/e2, offset + (i * destNrElements));
                }
            }
        }
    }

    public Matrix<T> transpose(int ... swap) {
        checkSwapPermutation(swap);
        int[] newDimensions = new int[dimensions().length];
        for (int i = 0; i < newDimensions.length; i++) {
            newDimensions[i] = dimensions()[swap[i]];
        }

        int from = 0;
        boolean first;
        byte[] next;
        byte[] overwritten = new byte[elementSize()];

        HashSet<Integer> visited = new HashSet<>();
        Matrix<T> transposed = createEmptyMatrix(newDimensions);

        for (int i = 0; i <= size(); i++) {
            first = true;
            while (!visited.contains(from)) {
                int[] fromCoords = indexToAddress(dimensions, from);
                int to = addressToIndex(swapCoords(fromCoords, swap), newDimensions, 0);
                int destPos = to * elementSize();

                next = overwritten.clone();
                System.arraycopy(data, to* elementSize(), overwritten, 0, elementSize());
                if (first) {
                    System.arraycopy(data, from * elementSize(), transposed.data(), to* elementSize(), elementSize());
                } else {
                    System.arraycopy(next, 0, transposed.data(), to * elementSize(), elementSize());
                }
                visited.add(from);
                from = to;
                first = false;
            }
            from = i;
        }

        return transposed;
    }

    private void checkSwapPermutation(int[] swap) {
        if (swap.length != dimensions().length) {
            throw new IncompatibleDimensionsException("Incompatible swap parameters.");
        }

        HashSet<Integer> temp = new HashSet<>();
        for (int s : swap) {
            if (s >= dimensions().length) {
                throw new IncompatibleDimensionsException("Swap parameter targets dimensions outside of bounds for this matrix.");
            }
            temp.add(s);
        }

        if (temp.size() != swap.length) {
            throw new IllegalArgumentException("All swap parameters have to be distinct from each other.");
        }
    }

    private int[] swapCoords(int[] coords, int[] swap) {
        if (coords.length != swap.length) {
            throw new IncompatibleDimensionsException("Value coordinates and swap dimensions do not match");
        }
        int[] result = new int[coords.length];
        for (int i = 0; i < swap.length; i++) {
            result[i] = coords[swap[i]];
        }
        return result;
    }

    private int[] indexToAddress(int[] dimensions, int idx) {
        int[] coords = new int[dimensions.length];
        int remainder = idx;

        for (int i = 0; i < dimensions.length-1; i++) {
            int p = Arrays.stream(dimensions).skip(i+1).reduce(1, (a,b) -> a * b);
            coords[i] = remainder / p;
            remainder = remainder % p;
        }
        coords[coords.length - 1] = remainder;

        return coords;
    }

    private int addressToIndex(int[] coords, int[] dimensions, int start){
        if (coords.length != dimensions.length) {
            throw new IncompatibleDimensionsException("Coordinates and target matrix dimensions do not match");
        }
        int address = 0;
        for (int i = start; i < start + dimensions.length; i++) {
            int a = coords[i];
            for (int j = i+1; j < coords.length; j++) {
                a *= dimensions[j];
            }
            address += a;
        }
        return address;
    }

    protected abstract Matrix<T> createEmptyMatrix(int[] dimensions);

    protected abstract T reduceLanes(Vector<T> vector, VectorOperators.Associative op);

    protected abstract T getDefaultValue();

    protected abstract T add(T t1, T t2);

    protected abstract void set(T value, int address);

    protected int elementSize() {
        // divide by 8, as elementSize() is in bits, not bytes
        return species.elementSize() / 8;
    }

    @Override
    public void set(T value, int... position) {
        if (position.length % dimensions.length != 0) {
            throw new IllegalArgumentException("Excepts positions dimension coordinate format");
        }

        for (int i = 0; i < position.length; i+=dimensions.length) {
            int baseAddress = addressToIndex(position, dimensions, i);
            set(value, baseAddress);
        }
    }

    @Override
    public void setDimensions(int[] dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    public int[] dimensions() {
        return dimensions;
    }

    @Override
    public int size() {
        return data.length * 8 / species.elementSize();
    }

    @Override
    public byte[] data() {
        return data;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        writeType(out);
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeByte(dimensions.length);
        for (int dim : dimensions) {
            dataOutputStream.writeInt(dim);
        }
        dataOutputStream.writeInt(data.length / elementSize());
        out.write(data);
    }

    protected abstract void writeType(OutputStream out) throws IOException;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InMemoryMatrix<?> that = (InMemoryMatrix<?>) o;

        if (!Objects.equals(species, that.species)) {
            return false;
        }

        if (!Arrays.equals(dimensions, that.dimensions)) {
            return false;
        }
        return Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = species != null ? species.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(dimensions);
        return result;
    }

    private <O extends Number> void ensureSameDimensions(Matrix<O> other) {
        if (!Arrays.equals(dimensions, other.dimensions())) {
            throw new IncompatibleDimensionsException("Dimensions do not match");
        }
    }

    protected abstract static class AbstractBuilder<T extends Number> implements Builder<T> {

        private byte[] data = new byte[1024]; // holds 1024 / 8 = 128 doubles
        private int rows = 0;
        private int columns = -1;

        @Override
        public AbstractBuilder<T> row(T... row) {
            if (columns != -1 && row.length != columns) {
                throw new IncompatibleDimensionsException("Subsequent calls must have same columns");
            }
            if (columns == -1) {
                columns = row.length;
            }

            rows++;
            int requiredSize = rows * columns;
            ensureCapacity(requiredSize);
            int offset = (rows - 1) * columns;
            fill(offset, data, row);
            return this;
        }

        protected abstract void fill(int offset, byte[] data, T... row);

        @Override
        public Matrix<T> build() {
            if (columns == -1) {
                throw new IllegalStateException("Must at least add 1 row");
            }
            byte[] validBytes = Arrays.copyOfRange(data, 0, rows * columns * byteSize());
            return doBuild(validBytes, rows, columns);
        }

        protected abstract Matrix<T> doBuild(byte[] data, int rows, int columns);

        private void ensureCapacity(int requiredSize) {
            if (data.length >= requiredSize * byteSize()) {
                return;
            }
            int newSize = requiredSize * 2 * byteSize();
            data = Arrays.copyOf(data, newSize);
        }

        protected abstract int byteSize();
    }
}
