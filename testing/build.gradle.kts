plugins {
    alias(libs.plugins.docuvault.android.library)
}

android {
    namespace = "com.grappim.docuvault.testing"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    api(libs.junit4)
    api(libs.kotlinx.coroutines.test)
    api(libs.turbine)
    api(libs.mockk)
    api(libs.mockk.android)
    api(libs.androidx.arch.core.testing)
    api(libs.androidx.test.runner)
    api(libs.androidx.test.rules)
    api(libs.androidx.test.espresso.core)
    api(libs.androidx.test.core)
    api(libs.androidx.compose.ui.test)

    debugApi(libs.androidx.compose.ui.testManifest)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}
