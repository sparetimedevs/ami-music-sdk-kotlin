import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.kotlin.dsl.commonMain
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.spotless)
    id("convention.publication")
    id("org.openapi.generator") version "7.13.0"
}

val publishedGroupId: String by project
val libraryVersion: String by project

project.group = publishedGroupId

project.version = libraryVersion

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    explicitApi()
    jvmToolchain { this.languageVersion.set(JavaLanguageVersion.of(11)) }
    jvm {
        compilations.all { kotlinOptions.jvmTarget = "11" }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            filter {
                // Exclude all compatibility tests
                excludeTestsMatching("backwardscompat.*")
            }
        }
    }
    js(IR) { browser { commonWebpackConfig { cssSupport { enabled.set(true) } } } }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget =
        when {
            hostOs == "Mac OS X" -> macosX64()
            hostOs == "Linux" -> linuxX64()
            isMingwX64 -> mingwX64()
            else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
        }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.arrow.core)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.assertions.json)
            implementation(libs.kotest.assertions.arrow)
        }
        jvmTest.dependencies {
            implementation(libs.okhttp)
        }
        // Add generated sources to the commonMain source set
        commonMain.get().kotlin.srcDir("$buildDir/generated/src/commonMain/kotlin")
    }
}

afterEvaluate {
    configure<PublishingExtension> {
        publications.all {
            val mavenPublication = this as? MavenPublication
            mavenPublication?.artifactId =
                "${project.name}${"-$name".takeUnless { "kotlinMultiplatform" in name }.orEmpty()}"
        }
    }
}

configure<SpotlessExtension> {
    kotlin {
        target("**/kotlin/**/*.kt")
        targetExclude("**/build/**/*.*")
        ktlint()
        licenseHeaderFile("$rootDir/LICENSE.header.template")
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}

afterEvaluate {
    val spotlessApply = tasks.findByName("spotlessApply")
    tasks.withType<KotlinCompile> { dependsOn(spotlessApply) }
}

val compatibilityTest by
    tasks.registering(Test::class) {
        description = "Runs compatibility tests."
        group = "verification"

        javaLauncher.set(
            javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(11)) },
        )

        useJUnitPlatform()
        testClassesDirs =
            kotlin
                .jvm()
                .compilations
                .get("test")
                .output.classesDirs
        classpath +=
            objects
                .fileCollection()
                .from(
                    tasks.named("compileKotlinJvm"),
                    tasks.named("compileTestKotlinJvm"),
                    configurations.named("jvmRuntimeClasspath"),
                    configurations.named("jvmTestRuntimeClasspath"),
                )

        filter {
            // Exclude all unit tests
            excludeTestsMatching("com.sparetimedevs.ami.*")
            // Include all compatibility tests
            includeTestsMatching("backwardscompat.*")
        }
    }

openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set("$rootDir/openapi/ami-music-spec.yaml")
    outputDir.set("$buildDir/generated")
    apiPackage.set("com.sparetimedevs.ami.music.api")
    modelPackage.set("com.sparetimedevs.ami.music.serialization")
    configOptions.set(
        mapOf(
            "dateLibrary" to "kotlinx-datetime",
            "explicitApi" to "false",
            "library" to "multiplatform",
            "sourceFolder" to "src/commonMain/kotlin",
        ),
    )
    // Skip API generation, only generate models
    generateApiDocumentation.set(false)
    generateApiTests.set(false)
    generateModelTests.set(false)
    generateModelDocumentation.set(false)
    // Skip API generation entirely
    globalProperties.set(
        mapOf(
            "apis" to "",
            "models" to "",
            "supportingFiles" to "false",
        ),
    )
}

// Make sure the OpenAPI code is generated before compilation
// tasks.withType<KotlinCompile> {
//    dependsOn("openApiGenerate")
// }
