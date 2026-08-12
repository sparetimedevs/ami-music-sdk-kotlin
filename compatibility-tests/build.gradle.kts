plugins {
    kotlin("jvm")
    `java-test-fixtures`
}

repositories {
    mavenCentral()
    maven {
        name = "centralSnapshots"
        url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        mavenContent {
            snapshotsOnly()
        }
    }
    mavenLocal()
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    testFixturesImplementation(kotlin("test-junit5"))
    testFixturesImplementation("org.junit.jupiter:junit-jupiter-params")
    testFixturesImplementation(libs.arrow.core)
    testFixturesImplementation(libs.kotlinx.serialization.json)
    testFixturesImplementation(libs.kotest.assertions.core)
    testFixturesImplementation(libs.kotest.assertions.json)
    testFixturesImplementation(libs.kotest.assertions.arrow)
}

testing {
    suites {
        register<JvmTestSuite>("compatibilitySdkCurrent") {
            dependencies {
                implementation(project(":"))
                implementation(testFixtures(project()))
                implementation(libs.arrow.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }

        register<JvmTestSuite>("compatibilitySdkV0_0_1_Snapshot") {
            dependencies {
                implementation("com.sparetimedevs.ami:ami-music-sdk-kotlin:0.0.1-SNAPSHOT")
                implementation(testFixtures(project()))
                implementation(libs.arrow.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }

        register<JvmTestSuite>("compatibilitySdkV0_0_2_Snapshot") {
            dependencies {
                implementation("com.sparetimedevs.ami:ami-music-sdk-kotlin:0.0.1-SNAPSHOT")
                implementation(testFixtures(project()))
                implementation(libs.arrow.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        // later add more versions…

        withType<JvmTestSuite>().configureEach {
            targets.configureEach {
                testTask.configure {
                    systemProperty("rootProjectDir", rootProject.projectDir.absolutePath)
                }
            }
        }
    }
}

afterEvaluate {
    tasks.named("check").configure {
        dependsOn(tasks.named("compatibilitySdkCurrent"))
        dependsOn(tasks.named("compatibilitySdkV0_0_1_Snapshot"))
        dependsOn(tasks.named("compatibilitySdkV0_0_2_Snapshot"))
    }
}
