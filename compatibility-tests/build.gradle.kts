plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    jvmToolchain(11)
}

//dependencies {
//    testImplementation(kotlin("test"))
//    testImplementation(libs.arrow.core)
//    testImplementation(libs.kotlinx.serialization.json)
//    testImplementation(libs.kotest.assertions.core)
//    testImplementation(libs.kotest.assertions.json)
//    testImplementation(libs.kotest.assertions.arrow)
//}

testing {
    suites {
        register<JvmTestSuite>("compatibilitySdkV0_0_1_Snapshot") {
            useKotlinTest()
            dependencies {
                implementation("com.sparetimedevs.ami:ami-music-sdk-kotlin:0.0.1-SNAPSHOT")
                implementation(libs.arrow.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.assertions.json)
                implementation(libs.kotest.assertions.arrow)
            }
        }

        register<JvmTestSuite>("compatibilitySdkV0_0_2_Snapshot") {
            useKotlinTest()
            dependencies {
                implementation("com.sparetimedevs.ami:ami-music-sdk-kotlin:0.0.1-SNAPSHOT")
                implementation(libs.arrow.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.assertions.json)
                implementation(libs.kotest.assertions.arrow)
            }
        }
        // later add more versions…
    }
}

afterEvaluate {
    tasks.named("check").configure {
        dependsOn(tasks.named("compatibilitySdkV0_0_1_Snapshot"))
        dependsOn(tasks.named("compatibilitySdkV0_0_2_Snapshot"))
    }
}
