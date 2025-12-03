
import com.android.build.api.dsl.LibraryExtension
import com.example.lint.libs
import com.example.lint.versionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.Actions.with
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      apply(plugin = "com.android.library")
      apply(plugin = "org.jetbrains.kotlin.android")

      val libs = target.libs

      extensions.configure<LibraryExtension> {
        compileSdk = libs.versions.compileSdk.get().toInt()

        defaultConfig {
          minSdk = libs.versions.minSdk.get().toInt()
          targetSdk = libs.versions.targetSdk.get().toInt()

          testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
          testInstrumentationRunnerArguments["clearPackageData"] = "true"

          // TODO:
          //    // Stops the Gradle plugin’s automatic rasterization of vectors & uses SVGs only
          //    generatedDensities = []

          vectorDrawables.useSupportLibrary = true

          multiDexEnabled = true
        }

        sourceSets {
          getByName("main").java.srcDirs("src/main/kotlin")
          getByName("test").java.srcDirs("src/test/kotlin")
          getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
        }

        compileOptions.isCoreLibraryDesugaringEnabled = true

        buildFeatures.dataBinding = false
        buildFeatures.viewBinding = true

        packagingOptions.resources.excludes.addAll(
          setOf(
            "META-INF/LICENSE.md",
            "META-INF/LICENSE-notice.md",
          )
        )
      }

      // TODO:
//      extensions.configure<ApplicationBuildType> {
//        this.extensions.configure<LibraryBuildType>("release") {
//          isMinifyEnabled = false
//        }
//      }

      dependencies {
        val versionCatalog = target.versionCatalog

        "coreLibraryDesugaring"(versionCatalog.findLibrary("androidDesugarJdkLibs").get())
        "debugImplementation"(versionCatalog.findLibrary("androidxComposeBom").get())

        "debugImplementation"(versionCatalog.findLibrary("androidxComposeUiTestManifest").get())

        "testImplementation"(versionCatalog.findLibrary("assertk").get())
        "androidTestImplementation"(versionCatalog.findLibrary("assertk").get())
      }
    }
  }
}