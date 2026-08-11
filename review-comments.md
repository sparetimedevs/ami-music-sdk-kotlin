# Review: compatibility-tests Module

## Summary

The compatibility-tests module establishes a good foundation for testing SDK compatibility across versions using Gradle's test suites and the `java-test-fixtures` plugin.

Update 2026-08-11: the frozen versioned JSON fixtures (`compatibility-tests/fixtures/vX.Y.Z/`) and the cross-version
test matrix are now in place, so the structure for true backward/forward compatibility testing exists. Remaining work
is tracked in the Priority Order at the bottom; the biggest outstanding caveat is that everything runs against the same
snapshot artifact and placeholder fixtures until real releases exist.

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

Done. Structure in place at `compatibility-tests/fixtures/vX.Y.Z/` (see its README). Until real releases exist, the
versioned directories contain placeholder copies of the current examples; snapshot real JSON per release going forward.

### 5. Expected Kotlin objects should be in testFixtures

The expected `Score` objects should be defined in `testFixtures` (not pulled from each SDK version) to serve as the canonical "truth". This way:
- You define once what the expected domain object looks like
- You test that each SDK version can deserialize to that same expected object
- Changes to example objects in the SDK don't silently change your test expectations

**Caveat — this is harder than it looks.** The canonical objects must be written in terms of `Score` and friends, so
`testFixtures` has to compile against *some* SDK version, while each test suite loads a *different* SDK version on its
classpath. Same fully-qualified class names coming from different artifacts is exactly the kind of classpath conflict
that turns into confusing failures. Solvable (e.g., express expectations as JSON-comparable data, or generate per-suite
sources), but it deserves design thought before implementing. Note that once frozen JSON fixtures (#4) are the truth,
the `shouldEqualJson` round-trip check carries most of the correctness weight this item was aiming for anyway.

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

Done. (The TODO was removed when the versioned fixtures from #4 were added.)

---

## Suggestions

### A. Restructure for true compatibility testing

Partially done. The versioned-fixtures half is in place (#4). The remaining half — canonical `CanonicalExamples`
objects in `testFixtures` — is item #5; see the classpath caveat there before implementing.

### B. Add cross-version matrix testing

Done, structurally. Every suite reads its own version's fixtures (baseline), older fixtures (backward) and newer
fixtures plus the current examples (forward). Becomes meaningful once real released versions are pinned (#1/#2).

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

(Revised 2026-08-11: items 1/2 from the original list are deferred until real releases exist; error reporting and
versioned JSON fixtures are done.)

1. **Parameterize tests** (#8) — small, quick win so one failing example doesn't hide the rest.
2. **Resolve fixture paths from the project root** (#6) — matters more now that fixtures are a real directory structure.
3. **Move expected objects to testFixtures** (#5) — revisit with the classpath caveat (see #5) in mind.
4. **Pin real released versions** (#1/#2) — once 0.0.1 is actually released.

**Release-checklist habit:** every time a release is cut, snapshot its serialized JSON into a new `fixtures/vX.Y.Z/`
directory as part of the release checklist. That's what makes the backward-compat matrix grow on its own.
