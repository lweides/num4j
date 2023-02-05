# num4j
Library to efficiently perform matrix operations, similar to `numpy` in Python.

## Usage
We recommend JDK 18. As this library uses `jdk.incubator.vector`,
`java` has to be invoked with the command line arguments `--add-modules jdk.incubator.vector`.

## Goal
The goal of this project was to use a Java preview feature, the `Vector API`.
It can be used to generate optimized machine code, which utilizes `SIMD` (Single Instruction Multiple Data) registers.
It is (obviously) inspired by `numpy`, and tries to mimic some (though very little) of its implementation.

While `num4j` is inspired by `numpy`, its main goal was to experiment with the Java `Vector API`.
Thus, performance and feature-richness were not a non-goal.

## Implementation
`num4j`s base data structure is a flat `byte[]` (`data`), which is filled with representations of `int`s and `double`s.
To access elements, an `int[]` servers as dimensions. Setting individual elements of `data` is done using `Unsafe`.
(Again, not for performance reasons - just out of curiosity.)
Inplace methods, like `add` and `mul` are implemented using the `Vector API`.
Aside from inplace methods, `mmul` and `transpose` both return new matrices.

`num4j` also supports memory mapped matrices. Huge matrices can easily be loaded, without running out of (Java) heap space.
Be advised that these matrices are unmodifiable.

## Usage
### Inplace operations
This section features some small examples of inplace operations:
```java
Matrix<Integer> a = IntegerMatrix.builder()
    .row(1, 2, 3)
    .row(4, 5, 6)
    .build();
```
This example creates the following integer matrix:
````
    1   2   3
    4   5   6
````

```java
Matrix<Integer> b = IntegerMatrix.ones(2, 3); // matrix with 2 rows and 3 columns, filled with 1's
a.add(b); // inplace and elementwise add
```

`a` now looks like this:
````
    2   3   4
    5   6   7
````

The other inplace operations work the same way.

### Transpose
The `transpose` method swaps elements in a matrix based on the input coordinates.
Each coordinate corresponds to a dimension, where 0 is associated to the highest dimension, 1 to the second highest and so on.
In its original form every matrix has the following order of coordinates.
Note that the example corresponds to a 3D matrix
````
0 1 2
````
If we want to transpose the matrix by the third and first dimension, we call:
```java
matrix.transpose(2, 1, 0);
```

In the following example we apply a `transpose` to a simple 2D matrix:
```java
Matrix<Integer> a = IntegerMatrix.builder()
    .row(1, 2, 3)
    .row(4, 5, 6)
    .build();

Matrix<Integer> b = a.transpose(1, 0);
```
This example creates the original matrix a:
````
    1   2   3
    4   5   6
````
and its transposed matrix b:
````
    1   4
    2   5
    3   6
````

### Matrix Multiplication
This section features a small example of a matrix multiplication:
```java
Matrix<Integer> a = IntegerMatrix.builder()
    .row(1, 2, 3)
    .row(4, 5, 6)
    .build();

Matrix<Integer> b = IntegerMatrix.builder()
    .row(1, 2)
    .row(3, 4)
    .row(5, 6)    
    .build();

Matrix<Integer> c = a.mmul(b);
```
This example computes the following matrix c:
````
    22   28
    49   64
````
### Building higher dimensional matrices
In order to create n-dimensional matrices, all elements of the matrix are initially defined in a 2D matrix,
followed by a `reshape` to its desired dimensions.
(note: for better visualization you can split up the elements in separate rows):
```java
Matrix<Integer> a = IntegerMatrix.builder()
                .row(1, 2)
                .row(3, 4)

                .row(5, 6)
                .row(7, 8)

                .row(9, 10)
                .row(11, 12)
                .build();
a.reshape(3, 2, 2);
```
The following matrix is created (numpy output form just for visualization):

````
    [[[1 2]
      [3 4]]
     
     [[5 6]
      [7 8]]
     
     [[9 10]
      [11 12]]]            
````

### Memory mapped matrices
`num4j` also allows memory mapping matrices:
```java
MemoryMappedMatrix<Integer> m = MemoryMappedIntegerMatrix.from(Path.of("path", "to", "matrix"));

a.add(m); // works as expected
m.add(a); // throws unmodifiable exception, as m is unmodifiable
m.close();
```

## Tools
`num4j` uses `maven` as a dependency management system (quite useful when having to specify flags for `java`).
Additionally, we provided some unit tests, which use `junit`.

## Contributors
Elias Rechberger <br />
Lukas Weidinger