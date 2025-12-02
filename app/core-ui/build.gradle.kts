plugins {
  id("cfa.compose-library")
  id("kotlin-parcelize")
}

android {
  namespace = "com.example.lint.coreui"
  testNamespace = "com.example.lint.coreui.test"
}

dependencies {
  api(libs.androidxAppcompat)
  implementation(libs.accompanistDrawablepainter)
  implementation(libs.kotlinxCoroutinesAndroid)
}
