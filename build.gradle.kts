import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import com.diffplug.gradle.spotless.SpotlessPlugin
import com.diffplug.spotless.LineEnding
import org.gradle.api.JavaVersion.VERSION_21
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  alias(libs.plugins.agpApp) apply false
  alias(libs.plugins.agpLibrary) apply false
  alias(libs.plugins.kotlin) apply false
  alias(libs.plugins.compose) apply false
  alias(libs.plugins.spotless)
  alias(libs.plugins.kotlinAndroid) apply false
}

val ktfmtVersion = libs.versions.ktfmt.get()

allprojects {
  pluginManager.apply(SpotlessPlugin::class)
  spotless {
    format("misc") {
      target("*.md", ".gitignore", "src/**/*.xml", "gradle/libs.versions.toml")
      trimTrailingWhitespace()
      endWithNewline()
      lineEndings = LineEnding.UNIX
    }

    kotlin {
      target("src/**/*.kt")
      targetExclude("src/gen/**/*.kt")
      ktfmt(ktfmtVersion).googleStyle()
      trimTrailingWhitespace()
      endWithNewline()
      lineEndings = LineEnding.UNIX
    }

    kotlinGradle {
      target("*.kts", "buildSrc/**/*.kts")
      ktfmt(ktfmtVersion).googleStyle()
      trimTrailingWhitespace()
      endWithNewline()
      lineEndings = LineEnding.UNIX
    }
  }

  tasks.register("printConfigurations") {
    doLast {
      println("'$project.name' configurations:")
      configurations.forEach { println("\t- $it.name") }
    }
  }
}

subprojects {
  pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    configure<KotlinJvmProjectExtension> { compilerOptions.jvmTarget.set(JVM_21) }
  }

  pluginManager.withPlugin("org.jetbrains.kotlin.android") {
    configure<KotlinAndroidProjectExtension> { compilerOptions.jvmTarget.set(JVM_21) }
  }

  pluginManager.withPlugin("java") {
    configure<JavaPluginExtension> {
      sourceCompatibility = VERSION_21
      targetCompatibility = VERSION_21
    }
  }

  pluginManager.withPlugin("com.android.application") {
    configure<ApplicationExtension> {
      compileOptions {
        sourceCompatibility = VERSION_21
        targetCompatibility = VERSION_21
      }

      lint {
        configureGlobalSettings(project)
        checkDependencies = true
      }
    }
  }

  pluginManager.withPlugin("com.android.library") {
    configure<LibraryExtension> {
      compileOptions {
        sourceCompatibility = VERSION_21
        targetCompatibility = VERSION_21
      }

      lint { configureGlobalSettings(project) }
    }
  }

  pluginManager.withPlugin("com.android.lint") {
    configure<Lint> { configureGlobalSettings(project) }
  }

  tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
      freeCompilerArgs.addAll(
        "-opt-in=kotlin.RequiresOptIn",
        "-Xjspecify-annotations=strict",
        "-Xtype-enhancement-improvements-strict-mode",
      )

      if (properties.containsKey("android.injected.invoked.from.ide")) {
        freeCompilerArgs.add("-Xdebug")
      }
    }
  }

  tasks.withType<Test>().configureEach {
    jvmArgs(
      "--add-opens",
      "java.base/java.time=ALL-UNNAMED",
      // Removes an erroneous error log with OpenJDK:
      // "Sharing is only supported for boot loader classes because bootstrap
      // classpath has been appended"
      "-Xshare:off",
    )

    testLogging {
      exceptionFormat = TestExceptionFormat.FULL
      showStackTraces = true
    }
  }
}

private fun Lint.configureGlobalSettings(project: Project) {
  warningsAsErrors = true
  explainIssues = false
  showAll = false

  lintConfig = rootProject.file("lint.xml")

  sarifReport = false
  xmlReport = false
  textReport = true
  htmlReport = true

  project.dependencies {
    add("lintChecks", rootProject.libs.slackComposeLintChecks)
    add("lintChecks", rootProject.libs.assertkLint)

    // TODO:
//    add("lintChecks", projects.infrastructure.lint)
  }
}
