# Review: compatibility-tests Module

## Summary

The compatibility-tests module establishes a good foundation for testing SDK compatibility across versions using Gradle's test suites and the `java-test-fixtures` plugin. However, there are several issues that prevent it from achieving true backward/forward compatibility testing.

---

## Critical Issues

### 1. Version mismatch in build.gradle.kts (line 46)

Not an issue as soon as we have real different versions.

### 2. All test implementations are identical

Not an issue as soon as we have real different versions.

### 3. Example Kotlin objects come from the tested SDK

Each test suite pulls `getExampleScore0()` and `getExampleScoreHeighHoNobodyHome()` from the SDK version being tested:

```kotlin
implementation("com.sparetimedevs.ami:ami-music-sdk-kotlin:0.0.1-SNAPSHOT")
// ...
"../openapi/examples/Score_...json" to getExampleScore0()  // getExampleScore0() comes from 0.0.1-SNAPSHOT
```

If the SDK's internal example objects change between versions, the tests will pass trivially because both sides of the comparison come from the same version.

---

## Design Issues

### 4. No versioned JSON test fixtures

For true compatibility testing, you need:
- **Backward compatibility**: Current SDK can deserialize JSON written by older SDK versions
- **Forward compatibility**: Older SDK can deserialize JSON written by current SDK

Currently, all suites use the same `../openapi/examples/` JSON files. Consider organizing like:

```
compatibility-tests/
  fixtures/
    v0.0.1/
      Score_example1.json  # JSON as serialized by v0.0.1
    v0.0.2/
      Score_example1.json  # JSON as serialized by v0.0.2
    current/
      Score_example1.json  # JSON from current version
```

### 5. Expected Kotlin objects should be in testFixtures

The expected `Score` objects should be defined in `testFixtures` (not pulled from each SDK version) to serve as the canonical "truth". This way:
- You define once what the expected domain object looks like
- You test that each SDK version can deserialize to that same expected object
- Changes to example objects in the SDK don't silently change your test expectations

### 6. Relative file paths are fragile

```kotlin
"../openapi/examples/Score_d737b4ae-fbaa-4b0d-9d36-d3651e30e93a.json"
```

This works only when tests run from `compatibility-tests/` directory. Consider using resource loading or absolute paths resolved from project root.

---

## Code Quality Issues

### 7. Error information lost on toJson failure

Done.

### 8. Single test method for all examples

If one example fails, the `forEach` stops and you don't see results for remaining examples. Consider using JUnit 5's `@ParameterizedTest` with `@MethodSource` or Kotest's data-driven testing to run each example as a separate test case.

### 9. Missing package declarations

The `CompatibilityTest.kt` files have no package declaration (default package). This works but is unconventional. Consider adding explicit packages like:

```kotlin
package com.sparetimedevs.ami.compat.current
package com.sparetimedevs.ami.compat.v0_0_1
package com.sparetimedevs.ami.compat.v0_0_2
```

### 10. TODO comment left in code

```kotlin
// TODO add list of old versions of JSON files.
```

This suggests the versioned JSON approach was intended but not implemented.

---

## Suggestions

### A. Restructure for true compatibility testing

```kotlin
// In testFixtures - canonical expected objects
object CanonicalExamples {
    val score0: Score = Score(...)  // Define the expected structure explicitly
}

// In each version's test
override fun examples(): Map<String, Score> = mapOf(
    "fixtures/v0.0.1/Score_example.json" to CanonicalExamples.score0,
    "fixtures/current/Score_example.json" to CanonicalExamples.score0,
)
```

### B. Add cross-version matrix testing

Test that:
1. v0.0.1 SDK can read v0.0.1 JSON (baseline)
2. v0.0.2 SDK can read v0.0.1 JSON (backward compat)
3. v0.0.1 SDK can read v0.0.2 JSON (forward compat, if supported)
4. Current SDK can read all previous JSON versions

### C. Consider property-based testing

For comprehensive compatibility testing, generate arbitrary `Score` objects, serialize with one version, deserialize with another, and verify round-trip equality.

---

## What Works Well

- Clean abstraction with `AbstractCompatibilityTest<Error, A>`
- Proper use of Gradle test suites for version isolation
- Good use of `java-test-fixtures` plugin for sharing test infrastructure
- Kotest assertions integration (`shouldBeRight`, `shouldEqualJson`)
- Clear separation between test fixtures and versioned test suites
- Hook into `check` task ensures compatibility tests run in CI

---

## Priority Order

1. **Fix version mismatch** in build.gradle.kts (bug)
2. **Improve error reporting** in AbstractCompatibilityTest (debugging)
3. **Add versioned JSON fixtures** (core functionality)
4. **Move expected objects to testFixtures** (test correctness)
5. **Parameterize tests** (test visibility)
