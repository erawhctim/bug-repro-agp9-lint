plugins {
  id("groovy-gradle-plugin")
  `kotlin-dsl`
}

repositories {
  // So that external plugins can be resolved as dependencies
  gradlePluginPortal()
}
