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
Elias Rechberger
Lukas Weidinger