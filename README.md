# Ami music SDK (Kotlin)

:construction: This SDK is still very experimental. API's are unstable and it's not feature complete. :construction:

A software development kit for working with music data.

The SDK contains:

- a collection of [common data models (CDM)](https://en.wikipedia.org/wiki/Common_data_model) describing the domain of
  music. In this case we focus on the conceptual data models. And they are strongly typed (meaning not describing
  concepts heavily using primitives but instead leveraging dedicated constructs).
- ways to serialize and deserialize the data.
- ways to validate serialized data when deserializing.

### Design principles

The SDK is designed with optionality in mind. You should be able to use all functionality, or just some, or none and
just use this as a source of inspiration. There is no risk of customer lock-in. You can move away from using Ami Music
SDK at anytime. (But really, there is no reason to not use the SDK.)
If you use this SDK to some or a larger extend, it should increase maintainability of what you are building, because the
functionality provided by the SDK is maintained by the maintainers of the SDK and its community.
Testability is another principle we keep in mind. Users of the SDK should be able to test their code and usage of the
SDK effectively. And of course, we make sure the SDK can and is well tested.
Moreover, the SDK is designed with extensibility in mind. Much of the functionality is extensible.

### Publish to local Maven repository

```
./gradlew build
# If you want to include sources, else next command can be skipped.
./gradlew sourcesJar
./gradlew publishToMavenLocal
```

### Publish to Maven Central repository

```
./gradlew publishAllPublicationsToSonatypeRepository
```

#### Use published artifact

`build.gradle.kts`

```
repositories {
	  ...
	  mavenLocal() // In case of locally published artifact.
	  maven("https://central.sonatype.com/repository/maven-snapshots/") // In case of snapshot artifact published to Maven Central snapshots repositories.
	  ...
}

dependencies {
    ...
	  implementation("com.sparetimedevs.ami:ami-music-sdk-kotlin:0.0.1-SNAPSHOT")
	  ...
}
```

To make a project use the latest snapshot version;

- remove `/.kotlin/` dir
- execute: `./gradlew clean build --refresh-dependencies`

### TypeSpec data models

This project has a dependency on the data models defined in `ami-music-spec`.

#### Include latest changes of `ami-music-spec` locally

(This assumes both projects `ami-music-spec` and `ami-music-sdk-kotlin` are in the same parent directory and your
current directory is `ami-music-sdk-kotlin`)

Execute:

```
(cd ../ami-music-spec && npm install)
(cd ../ami-music-spec && tsp compile .)
cp ../ami-music-spec/tsp-output/schema/openapi.yaml openapi/ami-music-spec.yaml
```

Now the OpenAPI spec included in this project is up to date.

Generate sources:

```
./gradlew openApiGenerate
```

Now the code in `generated` is up to date.

Extract examples as JSON:

```
./gradlew extractOpenApiExamples
```

Now the JSON examples in `openapi/examples` are up to date. 

### Compatibility tests

The `compatibility-tests` module tests that serialized music data (JSON) remains usable across SDK versions. Run them
with:

```
./gradlew compatibility-tests:check
```

Two directions of compatibility matter:

- **Backward compatibility**: the *current* SDK can deserialize JSON that was written by an *older* SDK version. This is
  tested by keeping JSON files as serialized by each released version and letting the current SDK read all of them.
- **Forward compatibility**: an *older* SDK version can deserialize JSON written by the *current* SDK. This is tested by
  letting test suites that depend on older, published SDK artifacts read the current JSON examples.

The module uses one Gradle test suite per SDK version (`compatibilitySdkCurrent`, `compatibilitySdkV0_0_1_Snapshot`,
...). Each suite pins its own SDK dependency and runs the same round-trip test (deserialize JSON, compare to the
expected Kotlin object, serialize back, compare to the JSON) via the shared `AbstractCompatibilityTest` in
`testFixtures`.

Note: until there are real, immutable releases, the versioned suites all resolve the same snapshot artifact, so the
setup currently showcases the mechanism rather than testing genuinely different versions. Once real releases exist, the
suites will pin those versions, and JSON files as serialized by each release will be kept as fixtures so both
directions are truly covered.
