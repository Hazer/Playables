import kotlin.String

/**
 * Generated by https://github.com/jmfayard/buildSrcVersions
 *
 * Update this file with
 *   `$ ./gradlew buildSrcVersions`
 */
object Libs {
    /**
     * https://github.com/Kotlin/kotlinx.serialization
     */
    const val kotlinx_serialization_runtime: String =
            "org.jetbrains.kotlinx:kotlinx-serialization-runtime:" +
            Versions.org_jetbrains_kotlinx_kotlinx_serialization

    /**
     * https://github.com/Kotlin/kotlinx.coroutines
     */
    const val kotlinx_coroutines_core: String = "org.jetbrains.kotlinx:kotlinx-coroutines-core:" +
            Versions.org_jetbrains_kotlinx_kotlinx_coroutines

    /**
     * https://github.com/reactivecircus/FlowBinding
     */
    const val flowbinding_android: String =
            "io.github.reactivecircus.flowbinding:flowbinding-android:" +
            Versions.io_github_reactivecircus_flowbinding

    /**
     * https://github.com/reactivecircus/FlowBinding
     */
    const val flowbinding_core: String = "io.github.reactivecircus.flowbinding:flowbinding-core:" +
            Versions.io_github_reactivecircus_flowbinding

    /**
     * https://github.com/reactivecircus/FlowBinding
     */
    const val flowbinding_material: String =
            "io.github.reactivecircus.flowbinding:flowbinding-material:" +
            Versions.io_github_reactivecircus_flowbinding

    /**
     * https://github.com/reactivecircus/FlowBinding
     */
    const val flowbinding_recyclerview: String =
            "io.github.reactivecircus.flowbinding:flowbinding-recyclerview:" +
            Versions.io_github_reactivecircus_flowbinding

    const val flowbinding_appcompat: String =
            "io.github.reactivecircus.flowbinding:flowbinding-appcompat:" +
            Versions.io_github_reactivecircus_flowbinding

    /**
     * http://github.com/square/leakcanary/
     */
    const val leakcanary_android: String = "com.squareup.leakcanary:leakcanary-android:" +
            Versions.com_squareup_leakcanary

    /**
     * http://github.com/square/leakcanary/
     */
    const val leakcanary_android_no_op: String =
            "com.squareup.leakcanary:leakcanary-android-no-op:" + Versions.com_squareup_leakcanary

    /**
     * https://developer.android.com/testing
     */
    const val espresso_contrib: String = "androidx.test.espresso:espresso-contrib:" +
            Versions.androidx_test_espresso

    /**
     * https://developer.android.com/testing
     */
    const val espresso_core: String = "androidx.test.espresso:espresso-core:" +
            Versions.androidx_test_espresso

    /**
     * https://developer.android.com/testing
     */
    const val espresso_intents: String = "androidx.test.espresso:espresso-intents:" +
            Versions.androidx_test_espresso

    /**
     * https://developer.android.com/testing
     */
    const val espresso_web: String = "androidx.test.espresso:espresso-web:" +
            Versions.androidx_test_espresso

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_android_extensions: String =
            "org.jetbrains.kotlin:kotlin-android-extensions:" + Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_android_extensions_runtime: String =
            "org.jetbrains.kotlin:kotlin-android-extensions-runtime:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_annotation_processing_gradle: String =
            "org.jetbrains.kotlin:kotlin-annotation-processing-gradle:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_gradle_plugin: String = "org.jetbrains.kotlin:kotlin-gradle-plugin:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_scripting_compiler_embeddable: String =
            "org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_serialization: String = "org.jetbrains.kotlin:kotlin-serialization:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_serialization_unshaded: String =
            "org.jetbrains.kotlin:kotlin-serialization-unshaded:" + Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_stdlib_jdk8: String = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://developer.android.com/topic/libraries/architecture/index.html
     */
    const val room_compiler: String = "androidx.room:room-compiler:" + Versions.androidx_room

    /**
     * https://developer.android.com/topic/libraries/architecture/index.html
     */
    const val room_ktx: String = "androidx.room:room-ktx:" + Versions.androidx_room

    /**
     * https://developer.android.com/topic/libraries/architecture/index.html
     */
    const val room_runtime: String = "androidx.room:room-runtime:" + Versions.androidx_room

    /**
     * https://developer.android.com/topic/libraries/architecture/index.html
     */
    const val room_testing: String = "androidx.room:room-testing:" + Versions.androidx_room

    /**
     * https://developer.android.com/testing
     */
    const val androidx_test_core: String = "androidx.test:core:" + Versions.androidx_test

    /**
     * https://developer.android.com/testing
     */
    const val androidx_test_rules: String = "androidx.test:rules:" + Versions.androidx_test

    /**
     * https://developer.android.com/testing
     */
    const val androidx_test_runner: String = "androidx.test:runner:" + Versions.androidx_test

    const val koin_android: String = "org.koin:koin-android:" + Versions.org_koin

    const val koin_androidx_scope: String = "org.koin:koin-androidx-scope:" + Versions.org_koin

    const val koin_androidx_viewmodel: String = "org.koin:koin-androidx-viewmodel:" +
            Versions.org_koin

    const val koin_core: String = "org.koin:koin-core:" + Versions.org_koin

    const val koin_test: String = "org.koin:koin-test:" + Versions.org_koin

    /**
     * https://github.com/ktorio/ktor
     */
    const val ktor_client_cio: String = "io.ktor:ktor-client-cio:" + Versions.io_ktor

    /**
     * https://github.com/ktorio/ktor
     */
    const val ktor_client_json_jvm: String = "io.ktor:ktor-client-json-jvm:" + Versions.io_ktor

    /**
     * https://github.com/ktorio/ktor
     */
    const val ktor_client_logging_jvm: String = "io.ktor:ktor-client-logging-jvm:" +
            Versions.io_ktor

    /**
     * https://github.com/ktorio/ktor
     */
    const val ktor_client_serialization_jvm: String = "io.ktor:ktor-client-serialization-jvm:" +
            Versions.io_ktor

    /**
     * https://developer.android.com/studio
     */
    const val com_android_tools_build_gradle: String = "com.android.tools.build:gradle:" +
            Versions.com_android_tools_build_gradle

    /**
     * https://developer.android.com/testing
     */
    const val androidx_test_ext_junit: String = "androidx.test.ext:junit:" +
            Versions.androidx_test_ext_junit

    const val io_fabric_tools_gradle: String = "io.fabric.tools:gradle:" +
            Versions.io_fabric_tools_gradle

    /**
     * http://junit.org
     */
    const val junit_junit: String = "junit:junit:" + Versions.junit_junit

    const val de_fayard_refreshversions_gradle_plugin: String =
            "de.fayard.refreshVersions:de.fayard.refreshVersions.gradle.plugin:" +
            Versions.de_fayard_refreshversions_gradle_plugin

    /**
     * http://developer.android.com/tools/extras/support-library.html
     */
    const val lifecycle_viewmodel_ktx: String = "androidx.lifecycle:lifecycle-viewmodel-ktx:" +
            Versions.lifecycle_viewmodel_ktx

    const val firebase_dynamic_links: String = "com.google.firebase:firebase-dynamic-links:" +
            Versions.firebase_dynamic_links

    /**
     * http://developer.android.com/tools/extras/support-library.html
     */
    const val lifecycle_livedata_ktx: String = "androidx.lifecycle:lifecycle-livedata-ktx:" +
            Versions.lifecycle_livedata_ktx

    const val lifecycle_runtime_ktx: String = "androidx.lifecycle:lifecycle-runtime-ktx:" +
            Versions.lifecycle_runtime_ktx

    /**
     * https://developer.android.com/topic/libraries/architecture/index.html
     */
    const val lifecycle_extensions: String = "androidx.lifecycle:lifecycle-extensions:" +
            Versions.lifecycle_extensions

    const val firebase_analytics: String = "com.google.firebase:firebase-analytics:" +
            Versions.firebase_analytics

    /**
     * https://developer.android.com/topic/libraries/architecture/index.html
     */
    const val lifecycle_compiler: String = "androidx.lifecycle:lifecycle-compiler:" +
            Versions.lifecycle_compiler

    /**
     * http://tools.android.com
     */
    const val constraintlayout: String = "androidx.constraintlayout:constraintlayout:" +
            Versions.constraintlayout

    /**
     * https://github.com/floschu/flow-test-extensions
     */
    const val flow_extensions: String = "at.florianschuster.test:flow-extensions:" +
            Versions.flow_extensions

    const val google_services: String = "com.google.gms:google-services:" + Versions.google_services

    /**
     * http://developer.android.com/tools/extras/support-library.html
     */
    const val collection_ktx: String = "androidx.collection:collection-ktx:" +
            Versions.collection_ktx

    const val ktlint_gradle: String = "org.jlleitschuh.gradle:ktlint-gradle:" +
            Versions.ktlint_gradle

    /**
     * https://developer.android.com/jetpack/androidx
     */
    const val activity_ktx: String = "androidx.activity:activity-ktx:" + Versions.activity_ktx

    /**
     * https://github.com/floschu/control
     */
    const val control_core: String = "at.florianschuster.control:control-core:" +
            Versions.control_core

    /**
     * https://github.com/akaita/easylauncher-gradle-plugin
     */
    const val easylauncher: String = "com.akaita.android:easylauncher:" + Versions.easylauncher

    /**
     * https://developer.android.com/jetpack/androidx
     */
    const val fragment_ktx: String = "androidx.fragment:fragment-ktx:" + Versions.fragment_ktx

    /**
     * https://github.com/chrisbanes/insetter/
     */
    const val insetter_ktx: String = "dev.chrisbanes:insetter-ktx:" + Versions.insetter_ktx

    const val crashlytics: String = "com.crashlytics.sdk.android:crashlytics:" +
            Versions.crashlytics

    /**
     * https://developer.android.com/studio
     */
    const val lint_gradle: String = "com.android.tools.lint:lint-gradle:" + Versions.lint_gradle

    /**
     * https://developer.android.com/jetpack/androidx
     */
    const val appcompat: String = "androidx.appcompat:appcompat:" + Versions.appcompat

    const val exoplayer: String = "com.google.android.exoplayer:exoplayer:" + Versions.exoplayer

    /**
     * http://developer.android.com/tools/extras/support-library.html
     */
    const val core_ktx: String = "androidx.core:core-ktx:" + Versions.core_ktx

    /**
     * https://github.com/floschu/lce-data
     */
    const val lce_data: String = "at.florianschuster.data:lce-data:" + Versions.lce_data

    /**
     * http://developer.android.com/tools/extras/support-library.html
     */
    const val material: String = "com.google.android.material:material:" + Versions.material

    /**
     * http://developer.android.com/tools/extras/support-library.html
     */
    const val browser: String = "androidx.browser:browser:" + Versions.browser

    /**
     * http://facebook.github.io/shimmer-android
     */
    const val shimmer: String = "com.facebook.shimmer:shimmer:" + Versions.shimmer

    /**
     * https://github.com/tailoredmedia/AndroidAppUtil
     */
    const val util_ui: String = "com.tailoredapps.androidutil:util-ui:" + Versions.util_ui

    /**
     * https://github.com/pinterest/ktlint
     */
    const val ktlint: String = "com.pinterest:ktlint:" + Versions.ktlint

    /**
     * https://github.com/JakeWharton/timber
     */
    const val timber: String = "com.jakewharton.timber:timber:" + Versions.timber

    /**
     * https://developer.android.com/studio
     */
    const val aapt2: String = "com.android.tools.build:aapt2:" + Versions.aapt2

    /**
     * http://mockk.io
     */
    const val mockk: String = "io.mockk:mockk:" + Versions.mockk

    /**
     * https://developer.android.com/testing
     */
    const val truth: String = "androidx.test.ext:truth:" + Versions.truth

    /**
     * https://github.com/coil-kt/coil
     */
    const val coil: String = "io.coil-kt:coil:" + Versions.coil
}
