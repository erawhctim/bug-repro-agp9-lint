import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  `kotlin-dsl`
//  alias(libs.plugins.lint)
}

// Configure the build-logic plugins to target JDK 21
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_21
  }
}

dependencies {
  compileOnly(libs.androidGradleApiPlugin)
  compileOnly(libs.androidToolsCommon)
  compileOnly(libs.composeGradlePlugin)
//  compileOnly(libs.firebase.crashlytics.gradlePlugin)
//  compileOnly(libs.firebase.performance.gradlePlugin)
//  compileOnly(libs.kotlin.gradlePlugin)
//  compileOnly(libs.ksp.gradlePlugin)
//  compileOnly(libs.room.gradlePlugin)
//  implementation(libs.truth)
//  lintChecks(libs.androidx.lint.gradle)

  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
  plugins {
    register("cfa.convention.library.compose") {
      id = libs.plugins.cfaAndroidLibraryCompose.get().pluginId
      implementationClass = "AndroidLibraryComposeConventionPlugin"
    }
    register("cfa.convention.library") {
      id = libs.plugins.cfaAndroidLibrary.get().pluginId
      implementationClass = "AndroidLibraryConventionPlugin"
    }
  }
}
