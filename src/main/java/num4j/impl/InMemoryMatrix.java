package num4j.impl;

import jdk.incubator.vector.*;
import num4j.api.Matrix;
import num4j.exceptions.IncompatibleDimensionsException;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashSet;

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
    private final int[] dimensions;

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
            Vector<T> va = fromByteArray(data, offset, mask);
            Vector<T> vo = fromByteArray(other.data(), offset, mask);

            va = va.add(vo);
            va.intoByteArray(data, offset, BYTE_ORDER, mask);
        }
    }

    public Matrix<T> mmul2D(Matrix<T> other) {
        if (dimensions().length != 2 && other.dimensions().length != 2) {
            throw new IllegalArgumentException("Require 2D Matrices");
        }

        if (dimensions()[1] != other.dimensions()[0]) {
            throw new IncompatibleDimensionsException("Matrix Multiplication: this.col and other.row have to be equal");
        }

        int m = dimensions()[0];
        int p = other.dimensions()[1];
        int n = dimensions()[1];
        Matrix<T> otherTransposed = other.transpose(1, 0);
        Matrix<T> result = createEmptyMatrix(new int[]{m,p});

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                T sum = getDefaultValue();
                for (int k = 0; k < n; k+= species.length()) {
                    VectorMask<T> mask = species.indexInRange(k, n);
                    Vector<T> va = fromByteArray(data(),elementSize() * (n * i +  k), mask);
                    Vector<T> vb = fromByteArray(otherTransposed.data(), elementSize() * (n * j + k), mask);

                    va = va.mul(vb);
                    sum = add(sum,reduceLanes(va, VectorOperators.ADD));
                }
                result.set(sum, i , j);
            }
        }

        return result;
    }

    public Matrix<T> transpose(int ... swap) {
        checkSwapPermutation(swap);
        int[] newDimensions = new int[dimensions().length];
        for (int i = 0; i < newDimensions.length; i++) {
            newDimensions[i] = dimensions()[swap[i]];
        }

        Matrix<T> transposed = createEmptyMatrix(newDimensions);
        int[] coordCounter = new int[dimensions().length];

        for (int i = 0; i < size(); i++) {
            int fromBase = computeAddress(coordCounter, dimensions(), 0) * elementSize();
            int[] coords = swapCoords(coordCounter, swap);
            int toBase = computeAddress(coords, transposed.dimensions(), 0) * elementSize();

            System.arraycopy(data, fromBase, transposed.data(), toBase, elementSize());

            incCounter(coordCounter, 0);
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

    private int computeAddress(int[] coords, int[] dimensions, int start){
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

    private void incCounter(int[] counters,int d) {
        int idx = Math.abs(d - counters.length+1);
        counters[idx] += 1;
        if (counters[idx] == dimensions()[idx]) {
            for (int i = idx; i < counters.length; i++) {
                counters[i] = 0;
            }
            incCounter(counters, d+1);
        }
    }

    protected abstract Vector<T> fromByteArray(byte[] data, int offset, VectorMask<T> m);

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
            int baseAddress = computeAddress(position, dimensions, i);
            set(value, baseAddress);
        }
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

    private <O extends Number> void ensureSameDimensions(Matrix<O> other) {
        if (!Arrays.equals(dimensions, other.dimensions())) {
            throw new IncompatibleDimensionsException("Dimensions do not match");
        }
    }
}
