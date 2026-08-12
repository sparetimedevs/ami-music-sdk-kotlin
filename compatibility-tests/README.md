# Compatibility tests

## Summary

The compatibility-tests module establishes a good foundation for testing SDK compatibility across versions using
Gradle's test suites and the `java-test-fixtures` plugin.

The frozen versioned JSON fixtures (`compatibility-tests/fixtures/vX.Y.Z/`) and the cross-version test matrix are now in
place, so the structure for true backward/forward compatibility testing exists. The biggest outstanding caveat is that
everything runs against the same snapshot artifact and placeholder fixtures until real releases exist.

**Release-checklist habit:** every time a release is cut, snapshot its serialized JSON into a new `fixtures/vX.Y.Z/`
directory as part of the release checklist. That's what makes the backward-compat matrix grow on its own.

## What Works Well

- Clean abstraction with `AbstractCompatibilityTest<Error, A>`
- Proper use of Gradle test suites for version isolation
- Good use of `java-test-fixtures` plugin for sharing test infrastructure
- Kotest assertions integration (`shouldBeRight`, `shouldEqualJson`)
- Clear separation between test fixtures and versioned test suites
- Hook into `check` task ensures compatibility tests run in CI

## Design Issues

### 1. Example Kotlin objects come from the tested SDK

Each test suite pulls `getExampleScore0()` and `getExampleScoreHeighHoNobodyHome()` from the SDK version being tested:

```kotlin
implementation("com.sparetimedevs.ami:ami-music-sdk-kotlin:0.0.1-SNAPSHOT")
// ...
"../openapi/examples/Score_...json" to getExampleScore0()  // getExampleScore0() comes from 0.0.1-SNAPSHOT
```

If the SDK's internal example objects change between versions, the tests will pass trivially because both sides of the
comparison come from the same version.

### 2. Expected Kotlin objects should be in testFixtures

The expected `Score` objects should be defined in `testFixtures` (not pulled from each SDK version) to serve as the
canonical "truth". This way:

- You define once what the expected domain object looks like
- You test that each SDK version can deserialize to that same expected object
- Changes to example objects in the SDK don't silently change your test expectations

**Caveat — this is harder than it looks.** The canonical objects must be written in terms of `Score` and friends, so
`testFixtures` has to compile against *some* SDK version, while each test suite loads a *different* SDK version on its
classpath. Same fully-qualified class names coming from different artifacts is exactly the kind of classpath conflict
that turns into confusing failures. Solvable (e.g., express expectations as JSON-comparable data, or generate per-suite
sources), but it deserves design thought before implementing. Note that once frozen JSON fixtures (#4) are the truth,
the `shouldEqualJson` round-trip check carries most of the correctness weight this item was aiming for anyway.

### 3. Relative file paths are fragile

```kotlin
"../openapi/examples/Score_d737b4ae-fbaa-4b0d-9d36-d3651e30e93a.json"
```

This works only when tests run from `` directory. Consider using resource loading or absolute paths resolved from
project root.

## Potential future improvements

### Consider property-based testing

For comprehensive compatibility testing, generate arbitrary `Score` objects, serialize with one version, deserialize
with another, and verify round-trip equality.
