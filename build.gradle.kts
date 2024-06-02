import com.diffplug.gradle.spotless.SpotlessExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotest.multiplatform)
    alias(libs.plugins.spotless)
    id("convention.publication")
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
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.assertions.json)
            implementation(libs.kotest.assertions.arrow)
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotest.framework.datatest)
        }
        jvmTest.dependencies {
            implementation(libs.kotest.runner.junit5)
            implementation(libs.okhttp)
        }
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
        ktfmt().kotlinlangStyle()
        licenseHeaderFile("$rootDir/LICENSE.header.template")
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktfmt().kotlinlangStyle()
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
            javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(11)) }
        )

        useJUnitPlatform()
        testClassesDirs = kotlin.jvm().compilations.get("test").output.classesDirs
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
