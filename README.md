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
	  maven("https://oss.sonatype.org/content/repositories/snapshots/") // In case of snapshot artifact published to Maven Central snapshots repositories.
	  ...
}

dependencies {
    ...
	  implementation("com.sparetimedevs.ami:ami-music-sdk-kotlin:0.0.1-SNAPSHOT)
	  ...
}
```

To make a project use the latest snapshot version, execute: `./gradlew clean build --refresh-dependencies`
