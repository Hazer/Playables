import kotlin.String

/**
 * Generated by https://github.com/jmfayard/buildSrcVersions
 *
 * Update this file with
 *   `$ ./gradlew buildSrcVersions`
 */
object Libs {
    /**
     * https://developer.android.com/jetpack/androidx
     */
    const val activity_ktx: String = "androidx.activity:activity-ktx:" + Versions.activity_ktx

    /**
     * https://developer.android.com/jetpack/androidx
     */
    const val appcompat: String = "androidx.appcompat:appcompat:" + Versions.appcompat

    /**
     * http://developer.android.com/tools/extras/support-library.html
     */
    const val browser: String = "androidx.browser:browser:" + Versions.browser

    /**
     * http://developer.android.com/tools/extras/support-library.html
     */
    const val collection_ktx: String = "androidx.collection:collection-ktx:" +
            Versions.collection_ktx

    /**
     * http://tools.android.com
     */
    const val constraintlayout: String = "androidx.constraintlayout:constraintlayout:" +
            Versions.constraintlayout

    /**
     * http://developer.android.com/tools/extras/support-library.html
     */
    const val core_ktx: String = "androidx.core:core-ktx:" + Versions.core_ktx

    /**
     * https://developer.android.com/jetpack/androidx
     */
    const val fragment_ktx: String = "androidx.fragment:fragment-ktx:" + Versions.fragment_ktx

    /**
     * https://developer.android.com/topic/libraries/architecture/index.html
     */
    const val lifecycle_compiler: String = "androidx.lifecycle:lifecycle-compiler:" +
            Versions.lifecycle_compiler

    /**
     * https://developer.android.com/topic/libraries/architecture/index.html
     */
    const val lifecycle_extensions: String = "androidx.lifecycle:lifecycle-extensions:" +
            Versions.lifecycle_extensions

    /**
     * http://developer.android.com/tools/extras/support-library.html
     */
    const val lifecycle_livedata_ktx: String = "androidx.lifecycle:lifecycle-livedata-ktx:" +
            Versions.lifecycle_livedata_ktx

    const val lifecycle_runtime_ktx: String = "androidx.lifecycle:lifecycle-runtime-ktx:" +
            Versions.lifecycle_runtime_ktx

    /**
     * http://developer.android.com/tools/extras/support-library.html
     */
    const val lifecycle_viewmodel_ktx: String = "androidx.lifecycle:lifecycle-viewmodel-ktx:" +
            Versions.lifecycle_viewmodel_ktx

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
     * https://developer.android.com/testing
     */
    const val androidx_test_ext_junit: String = "androidx.test.ext:junit:" +
            Versions.androidx_test_ext_junit

    /**
     * https://developer.android.com/testing
     */
    const val truth: String = "androidx.test.ext:truth:" + Versions.truth

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

    /**
     * https://github.com/floschu/control
     */
    const val control_core: String = "at.florianschuster.control:control-core:" +
            Versions.at_florianschuster_control

    /**
     * https://github.com/floschu/control
     */
    const val control_test: String = "at.florianschuster.control:control-test:" +
            Versions.at_florianschuster_control

    /**
     * https://github.com/floschu/lce-data
     */
    const val lce_data: String = "at.florianschuster.data:lce-data:" + Versions.lce_data

    /**
     * https://github.com/akaita/easylauncher-gradle-plugin
     */
    const val easylauncher: String = "com.akaita.android:easylauncher:" + Versions.easylauncher

    /**
     * https://developer.android.com/studio
     */
    const val aapt2: String = "com.android.tools.build:aapt2:" + Versions.aapt2

    /**
     * https://developer.android.com/studio
     */
    const val com_android_tools_build_gradle: String = "com.android.tools.build:gradle:" +
            Versions.com_android_tools_build_gradle

    /**
     * https://developer.android.com/studio
     */
    const val lint_gradle: String = "com.android.tools.lint:lint-gradle:" + Versions.lint_gradle

    /**
     * http://developer.android.com/tools/extras/support-library.html
     */
    const val material: String = "com.google.android.material:material:" + Versions.material

    /**
     * https://github.com/JakeWharton/ThreeTenABP/
     */
    const val threetenabp: String = "com.jakewharton.threetenabp:threetenabp:" +
            Versions.threetenabp

    /**
     * https://github.com/JakeWharton/timber
     */
    const val timber: String = "com.jakewharton.timber:timber:" + Versions.timber

    /**
     * https://github.com/pinterest/ktlint
     */
    const val ktlint: String = "com.pinterest:ktlint:" + Versions.ktlint

    /**
     * http://github.com/square/leakcanary/
     */
    const val leakcanary_android_no_op: String =
            "com.squareup.leakcanary:leakcanary-android-no-op:" + Versions.com_squareup_leakcanary

    /**
     * http://github.com/square/leakcanary/
     */
    const val leakcanary_android: String = "com.squareup.leakcanary:leakcanary-android:" +
            Versions.com_squareup_leakcanary

    /**
     * https://github.com/tailoredmedia/AndroidAppUtil
     */
    const val util_ui: String = "com.tailoredapps.androidutil:util-ui:" + Versions.util_ui

    /**
     * http://github.com/vanniktech/gradle-dependency-graph-generator-plugin/
     */
    const val gradle_dependency_graph_generator_plugin: String =
            "com.vanniktech:gradle-dependency-graph-generator-plugin:" +
            Versions.gradle_dependency_graph_generator_plugin

    const val buildsrcversions: String = "de.fayard:buildSrcVersions:" + Versions.buildsrcversions

    /**
     * https://github.com/nidi3/graphviz-java
     */
    const val graphviz_java: String = "guru.nidi:graphviz-java:" + Versions.graphviz_java

    /**
     * https://github.com/coil-kt/coil
     */
    const val coil: String = "io.coil-kt:coil:" + Versions.coil

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
     * http://mockk.io
     */
    const val mockk: String = "io.mockk:mockk:" + Versions.mockk

    /**
     * http://junit.org
     */
    const val junit_junit: String = "junit:junit:" + Versions.junit_junit

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_android_extensions_runtime: String =
            "org.jetbrains.kotlin:kotlin-android-extensions-runtime:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_android_extensions: String =
            "org.jetbrains.kotlin:kotlin-android-extensions:" + Versions.org_jetbrains_kotlin

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
    const val kotlin_serialization_unshaded: String =
            "org.jetbrains.kotlin:kotlin-serialization-unshaded:" + Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_serialization: String = "org.jetbrains.kotlin:kotlin-serialization:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://kotlinlang.org/
     */
    const val kotlin_stdlib_jdk8: String = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:" +
            Versions.org_jetbrains_kotlin

    /**
     * https://github.com/Kotlin/kotlinx.coroutines
     */
    const val kotlinx_coroutines_core: String = "org.jetbrains.kotlinx:kotlinx-coroutines-core:" +
            Versions.kotlinx_coroutines_core

    /**
     * https://github.com/Kotlin/kotlinx.serialization
     */
    const val kotlinx_serialization_runtime: String =
            "org.jetbrains.kotlinx:kotlinx-serialization-runtime:" +
            Versions.kotlinx_serialization_runtime

    const val ktlint_gradle: String = "org.jlleitschuh.gradle:ktlint-gradle:" +
            Versions.ktlint_gradle

    const val koin_android: String = "org.koin:koin-android:" + Versions.org_koin

    const val koin_androidx_scope: String = "org.koin:koin-androidx-scope:" + Versions.org_koin

    const val koin_androidx_viewmodel: String = "org.koin:koin-androidx-viewmodel:" +
            Versions.org_koin

    const val koin_core: String = "org.koin:koin-core:" + Versions.org_koin

    const val koin_test: String = "org.koin:koin-test:" + Versions.org_koin

    /**
     * https://github.com/LDRAlighieri/Corbind/
     */
    const val corbind_appcompat: String = "ru.ldralighieri.corbind:corbind-appcompat:" +
            Versions.ru_ldralighieri_corbind

    /**
     * https://github.com/LDRAlighieri/Corbind/
     */
    const val corbind_core: String = "ru.ldralighieri.corbind:corbind-core:" +
            Versions.ru_ldralighieri_corbind

    /**
     * https://github.com/LDRAlighieri/Corbind/
     */
    const val corbind_material: String = "ru.ldralighieri.corbind:corbind-material:" +
            Versions.ru_ldralighieri_corbind

    /**
     * https://github.com/LDRAlighieri/Corbind/
     */
    const val corbind_recyclerview: String = "ru.ldralighieri.corbind:corbind-recyclerview:" +
            Versions.ru_ldralighieri_corbind

    /**
     * https://github.com/LDRAlighieri/Corbind/
     */
    const val corbind: String = "ru.ldralighieri.corbind:corbind:" +
            Versions.ru_ldralighieri_corbind
}
