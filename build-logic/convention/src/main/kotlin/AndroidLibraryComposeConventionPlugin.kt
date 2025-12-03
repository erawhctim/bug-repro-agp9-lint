import com.android.build.api.dsl.LibraryExtension
import com.example.lint.libs
import com.example.lint.versionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    val libs = target.libs

    with(target) {
      apply(plugin = "org.jetbrains.kotlin.plugin.compose")
      apply(plugin = libs.plugins.cfaAndroidLibrary.get().pluginId)

      val libs = target.libs

      extensions.configure<LibraryExtension> {
        buildFeatures.compose = true

        // TODO:
//        composeCompiler {
//          if (hasProperty("composeReport")) {
//            metricsDestination = layout.buildDirectory.dir("compose-report")
//            reportsDestination = layout.buildDirectory.dir("compose-report")
//          }
//        }
      }

      extensions.configure<ComposeCompilerGradlePluginExtension> {

      }

      dependencies {
        val versionCatalog = target.versionCatalog

        "implementation"(platform(versionCatalog.findLibrary("androidxComposeBom").get()))
        "testImplementation"(platform(versionCatalog.findLibrary("androidxComposeBom").get()))

        "implementation"(versionCatalog.findLibrary("androidxComposeUi").get())
        "implementation"(versionCatalog.findLibrary("androidxComposeActivity").get())
        "implementation"(versionCatalog.findLibrary("accompanistSystemuicontroller").get())
        "implementation"(versionCatalog.findLibrary("androidxComposeConstraintLayout").get())

        "debugImplementation"(versionCatalog.findLibrary("androidxComposeUiTooling").get())
        "implementation"(versionCatalog.findLibrary("androidxComposeUiToolingPreview").get())
      }
    }

    // TODO: do we still need this dumb thing?
    //configurations {
    //  all*.exclude group: "com.google.guava", module: "listenablefuture"
    //}
  }
}