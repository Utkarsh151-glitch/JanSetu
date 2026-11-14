import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val releaseKeystore = providers.environmentVariable("JANSETU_KEYSTORE").orNull
val releaseStorePassword = providers.environmentVariable("JANSETU_STORE_PASSWORD").orNull
val releaseKeyAlias = providers.environmentVariable("JANSETU_KEY_ALIAS").orNull
val releaseKeyPassword = providers.environmentVariable("JANSETU_KEY_PASSWORD").orNull
val releaseSigningRequested = listOf(releaseKeystore, releaseStorePassword, releaseKeyAlias, releaseKeyPassword).any { it != null }
if (releaseSigningRequested) {
    require(listOf(releaseKeystore, releaseStorePassword, releaseKeyAlias, releaseKeyPassword).all { !it.isNullOrBlank() }) {
        "Set all JANSETU_* signing environment variables for a signed release"
    }
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val prepareRuntimeAssets by tasks.registering(Exec::class) {
    val output = layout.buildDirectory.dir("generated/runtimeAssets")
    inputs.dir(layout.projectDirectory.dir("src/main/assets"))
    inputs.file(rootProject.file("tools/package_catalog.py"))
    inputs.file(rootProject.file("data/category_groups.json"))
    inputs.file(rootProject.file("data/localizations/hi.json"))
    inputs.file(rootProject.file("tools/service_catalog.py"))
    inputs.dir(rootProject.file("data/services"))
    // Phase 6B: published website profiles are validated against their saved snapshots while packaging.
    inputs.file(rootProject.file("tools/website_enrichment.py"))
    inputs.file(rootProject.file("data/website_enrichment/claims.json"))
    inputs.dir(rootProject.file("data/website_enrichment/research"))
    // Phase 6C: evidence pins and human review decisions (append-only) also shape the packaged profiles.
    inputs.file(rootProject.file("tools/editorial_review.py"))
    inputs.file(rootProject.file("data/website_enrichment/evidence_index.json"))
    inputs.file(rootProject.file("data/website_enrichment/reviews.json"))
    outputs.dir(output)
    commandLine(providers.environmentVariable("JANSETU_PYTHON").getOrElse("python"),
        rootProject.file("tools/package_catalog.py").absolutePath, "assets", "--output", output.get().asFile.absolutePath)
}

android {
    namespace = "in.jansetu.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "in.jansetu.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 5
        versionName = "0.4.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resValue("string", "app_name", "JanSetu")
    }

    signingConfigs {
        if (releaseSigningRequested) create("release") {
            storeFile = file(releaseKeystore!!)
            storePassword = releaseStorePassword
            keyAlias = releaseKeyAlias
            keyPassword = releaseKeyPassword
        }
    }
    buildTypes {
        create("qa") {
            initWith(getByName("debug"))
            applicationIdSuffix = ".qa"
            versionNameSuffix = "-qa"
            resValue("string", "app_name", "JanSetu QA")
            matchingFallbacks += listOf("debug")
        }
        release {
            isMinifyEnabled = false
            if (releaseSigningRequested) signingConfig = signingConfigs.getByName("release")
        }
    }
    // Instrumentation and its Keystore/SQLite/preferences live in a separate UID.
    testBuildType = "qa"
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { compose = true }
    sourceSets.getByName("main").assets.setSrcDirs(listOf(layout.buildDirectory.dir("generated/runtimeAssets")))
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

tasks.named("preBuild") { dependsOn(prepareRuntimeAssets) }

kotlin {
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.03.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity-compose:1.12.3")
    implementation("androidx.navigation:navigation-compose:2.9.8")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.7.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4-accessibility")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    add("qaImplementation", "androidx.compose.ui:ui-test-manifest")
}

// JVM catalog tests read this asset directly; data changes must invalidate
// Gradle's cached test result even when no Kotlin source changed.
tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
    // Several suites parse the full 7,347-website catalog; the 512 MB default leaves too little headroom.
    maxHeapSize = "1g"
    outputs.upToDateWhen { !project.hasProperty("jansetu.forceTests") }
    inputs.file(layout.projectDirectory.file("src/main/assets/catalog.json"))
    inputs.dir(layout.buildDirectory.dir("generated/runtimeAssets"))
}

