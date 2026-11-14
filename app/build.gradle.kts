import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
}

android {
  namespace = "com.jarrodqsimpson.calmloop"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.jarrodqsimpson.calmloop"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    // Address of the stateless AI relay (see proxy/). Read from .env or the AI_PROXY_URL
    // environment variable; empty builds the app with AI reflections unavailable.
    val dotEnv =
      Properties().apply {
        val f = rootProject.file(".env")
        if (f.exists()) f.inputStream().use { load(it) }
      }
    val aiProxyUrl = (dotEnv.getProperty("AI_PROXY_URL") ?: System.getenv("AI_PROXY_URL") ?: "").trim()
    require(aiProxyUrl.isEmpty() || aiProxyUrl.startsWith("https://")) { "AI_PROXY_URL must use https://" }
    buildConfigField("String", "AI_PROXY_URL", "\"$aiProxyUrl\"")
  }

  // Release signing is read from keystore.properties (git-ignored) or environment variables.
  // Without either, release builds are produced unsigned and can be signed later.
  val keystoreProps =
    Properties().apply {
      val f = rootProject.file("keystore.properties")
      if (f.exists()) f.inputStream().use { load(it) }
    }
  fun signingValue(key: String, env: String): String? =
    keystoreProps.getProperty(key) ?: System.getenv(env)
  val releaseStoreFile = signingValue("storeFile", "KEYSTORE_PATH")?.let { rootProject.file(it) }

  signingConfigs {
    if (releaseStoreFile != null && releaseStoreFile.exists()) {
      create("release") {
        storeFile = releaseStoreFile
        storePassword = signingValue("storePassword", "STORE_PASSWORD")
        keyAlias = signingValue("keyAlias", "KEY_ALIAS") ?: "upload"
        keyPassword = signingValue("keyPassword", "KEY_PASSWORD")
      }
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.findByName("release")
    }
  }
  compileOptions {
    // java.time on Android 7 (minSdk 24) needs core library desugaring.
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
ksp { arg("room.schemaLocation", "$projectDir/schemas") }

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.okhttp)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.okhttp.mockwebserver)
  testImplementation(libs.org.json)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  coreLibraryDesugaring(libs.desugar.jdk.libs)
  "ksp"(libs.androidx.room.compiler)
}
