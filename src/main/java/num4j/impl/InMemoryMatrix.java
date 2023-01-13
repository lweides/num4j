package num4j.impl;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorSpecies;
import num4j.api.Matrix;
import num4j.exceptions.IncompatibleDimensionsException;

import java.util.Arrays;

public class InMemoryMatrix implements Matrix {

    /**
     * Creates a new matrix with the specified {@code dimensions}, filled with {@code 0}.
     * @param dimensions dimensions of the matrix
     * @return the newly created matrix
     */
    public static InMemoryMatrix zeros(int ... dimensions) {
        return new InMemoryMatrix(dimensions);
    }

    /**
     * Creates a new matrix with the specified {@code dimensions}, filled with {@code 1}.
     * @param dimensions dimensions of the matrix
     * @return the newly created matrix
     */
    public static InMemoryMatrix ones(int ... dimensions) {
        int nrElements = nrElements(dimensions);
        double[] data = new double[nrElements];
        Arrays.fill(data, 1);
        return new InMemoryMatrix(data, dimensions);
    }

    /**
     * @return number of elements a matrix with the specified {@code dimensions} contains.
     */
    private static int nrElements(int ... dimensions) {
        return Arrays.stream(dimensions)
            .reduce((i1, i2) -> i1 * i2)
            .orElse(0);
    }

    private static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_PREFERRED;

    private final double[] data;
    private final int[] dimensions;

    private InMemoryMatrix(int ... dimensions) {
        int nrElements = nrElements(dimensions);
        this.data = new double[nrElements];
        this.dimensions = dimensions;
    }

    private InMemoryMatrix(double[] data, int ... dimensions) {
        if (nrElements(dimensions) != data.length) {
            throw new IllegalArgumentException("Dimensions and size of data array do not match");
        }
        this.data = data;
        this.dimensions = dimensions;
    }

    @Override
    public void add(Matrix other) {
        ensureSameDimensions(other);
        double[] otherData = other.data();
        int upperBound = SPECIES.loopBound(size());
        int i = 0;

        for (; i < upperBound; i += SPECIES.length()) {
            var va = DoubleVector.fromArray(SPECIES, data, i);
            var vo = DoubleVector.fromArray(SPECIES, otherData, i);
            va = va.add(vo);
            va.intoArray(data, i);
        }

        for (; i < size(); i++) {
            data[i] += otherData[i];
        }
    }

    @Override
    public void set(double value, int... position) {
        data[offset(position)] = value;
    }

    @Override
    public int[] dimensions() {
        return dimensions;
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public double[] data() {
        return data;
    }

    private int offset(int ... position) {
        if (dimensions.length != position.length) {
            throw new IncompatibleDimensionsException("Must address exactly one entry");
        }
        int offset = 0;
        int dimOffset = 0;
        for (int i = 0; i < dimensions.length; i++) {
            int max = dimensions[i];
            int pos = position[i];
            if (pos < 0 || pos >= max) {
                throw new IncompatibleDimensionsException("Must not address value outside of matrix");
            }
            dimOffset += max;
        }
        // TODO think of layout
        return offset;
    }

    private void ensureSameDimensions(Matrix other) {
        if (!Arrays.equals(dimensions, other.dimensions())) {
            throw new IncompatibleDimensionsException("Dimensions do not match");
        }
    }
}
