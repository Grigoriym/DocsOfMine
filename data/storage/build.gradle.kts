plugins {
    alias(libs.plugins.docuvault.android.library)
    alias(libs.plugins.docuvault.android.hilt)
}

android {
    namespace = "com.grappim.docuvault.data.storage"
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.androidx.security.crypto)
}
