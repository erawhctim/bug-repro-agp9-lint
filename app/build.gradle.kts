plugins {
  alias(libs.plugins.agpApp)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.compose)
}

android {
  namespace = "com.example.lint"

  compileSdk = libs.versions.compileSdk.get().toInt()

  defaultConfig {
    applicationId = "ccom.example.lint"
    minSdk = libs.versions.minSdk.get().toInt()
    targetSdk = libs.versions.targetSdk.get().toInt()
    versionCode = 1
    versionName = "1.0"

    vectorDrawables { useSupportLibrary = true }

    buildFeatures.buildConfig = true
  }

  androidComponents {
    /**
     * exclude tools module for:
     * - production (except for productionDebug as this might be useful for troubleshooting)
     * - benchmark (so as not to profile code that doesn't make it to production)
     * - non-minified
     */
    onVariants { variant ->
      when (variant.name) {
        "devDebug",
        "devRelease",
        "productionDebug",
        "stagingDebug",
        "stagingRelease",
        "devBenchmarkRelease",
        "devNonMinifiedRelease",
        "stagingBenchmarkRelease",
        "stagingNonMinifiedRelease" ->
          sourceSets {
            named(variant.name).configure {
              java.srcDirs("src/tools/java")
              res.srcDirs("src/tools/res")

              val variantImplementation =
                requireNotNull(configurations.findByName("${variant.name}Implementation")) {
                  "Couldn't find ${variant.name}Implementation configuration, " +
                    "which shouldn't be possible."
                }

              // Make all the toolsImplementation deps. available to all the
              // variants that enable the tools menu
              variantImplementation.extendsFrom(toolsImplementation)
            }
          }
        "productionBenchmarkRelease",
        "productionNonMinifiedRelease",
        "productionPilotBenchmarkRelease",
        "productionPilotDebug",
        "productionPilotNonMinifiedRelease",
        "productionPilotRelease",
        "productionRelease" ->
          sourceSets {
            named(variant.name).configure {
              java.srcDirs("src/toolsRelease/java")
              res.srcDirs("src/toolsRelease/res")
            }
          }
        else ->
          error(
            "Determine if the tools menu should be in the ${variant.name} build variant and then update this list."
          )
      }
    }
  }

  signingConfigs {
    named("debug") {
      keyAlias = "androiddebugkey"
      keyPassword = "android"
      storePassword = "android"
      storeFile = file("../keystore/debug.keystore")
    }
    register("release") {
      if (project.properties.containsKey("signWithDebugKeystore")) {
        keyAlias = "androiddebugkey"
        keyPassword = "android"
        storePassword = "android"
        storeFile = file("../keystore/debug.keystore")
      } else {
        keyAlias = System.getenv("RELEASE_KEY_ALIAS")
        keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
        storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
        val keystorePath = System.getenv("RELEASE_KEYSTORE_PATH")
        if (keystorePath != null) {
          storeFile = file(keystorePath)
        }
      }
    }
  }

  buildTypes {
    debug {
      val packageSuffix = ".debug"
      applicationIdSuffix = packageSuffix
      testCoverage { enableUnitTestCoverage = project.hasProperty("coverage") }

      signingConfig = signingConfigs["debug"]
      manifestPlaceholders["engageSDKEnvironment"] = "DEBUG"
    }
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles("proguard-rules.pro")
      signingConfig = signingConfigs["release"]
      manifestPlaceholders["engageSDKEnvironment"] = "PRODUCTION"
    }
  }

  flavorDimensions += "environment"
  productFlavors {
    create("dev") {
      dimension = "environment"
    }
    create("staging") {
      isDefault = true
      dimension = "environment"
      applicationIdSuffix = ".staging"
    }
    create("production") {
      dimension = "environment"
    }
    create("productionPilot") {
      dimension = "environment"
    }
  }

  testOptions { animationsDisabled = true }

  // This enables Google Play's dynamic APK delivery for smaller APK downloads
  bundle {
    language { enableSplit = true }
    density { enableSplit = true }
    abi { enableSplit = true }
  }

  buildFeatures {
    viewBinding = true
    compose = true
  }

  compileOptions { isCoreLibraryDesugaringEnabled = true }

  composeCompiler {
    if (hasProperty("composeReport")) {
      metricsDestination = layout.buildDirectory.dir("compose-report")
      reportsDestination = layout.buildDirectory.dir("compose-report")
    }
  }

  packaging {
    resources.excludes.add("META-INF/LICENSE.md")
    resources.excludes.add("META-INF/LICENSE-notice.md")
  }
}

val toolsImplementation: Configuration by configurations.creating

dependencies {
  implementation(projects.app.coreUi)

  coreLibraryDesugaring(libs.androidDesugarJdkLibs)
  debugImplementation(libs.androidxComposeUiTooling)
  implementation(platform(libs.androidxComposeBom))
  implementation(libs.androidxComposeUi)
  implementation(libs.androidxComposeUiToolingPreview)
  implementation(libs.androidxComposeActivity)
  testImplementation(platform(libs.androidxComposeBom))
  testImplementation(libs.assertk)
  testImplementation(libs.assertk)
  androidTestImplementation(libs.assertk)
}
