pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

  /**
   * These repositories are evaluated top-down when attempting to resolve dependencies. More
   * commonly-used repository URLs should live at the top, and less-commonly-used URLs should be
   * placed at the bottom.
   */
  repositories {
    mavenCentral()
    google()

    // Google maven URL differs from the dl.google.com URL (below)
    maven(url = "https://maven.google.com")
    maven(url = "https://www.jitpack.io") {
      content { includeGroupByRegex("com\\.github\\.erawhctim*") }
    }

    // TODO: maven URL for Forter SDK removed.

    // CFA internal Artifactory hosting Buf Schema Registry
    maven(url = "https://cfa.jfrog.io/artifactory/maven-customer/") {
      name = "CFA internal Artifactory hosting Buf Schema Registry"
      credentials {
        // Local development should set the gradle properties in ~/.gradle/gradle.properties
        // CI builds should use the env. vars.
        username = settings.ext["artifactoryUser"].toString()
        password = settings.ext["artifactoryToken"].toString()
      }
      content { includeGroupByRegex("com\\.cfadevelop\\.buf\\.gen") }
    }
  }

  // MoEngage SDK
//  versionCatalogs { create("moengage") { from("com.moengage:android-dependency-catalog:6.1.1") } }
}

plugins {
  id("com.gradle.develocity").version("4.2.2")
}

develocity {
  buildScan {
    publishing.onlyIf { false } // only publish when explicitly requested by --scan
    termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
    termsOfUseAgree.set("yes")
  }
}

rootProject.name = "lint-gradle9-issue-repro"


enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
  ":app",
  ":app:core-ui",
)