package com.alefranc.composescreenshotplugin.testUtils

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import java.nio.file.Paths

const val KOTLIN_LANGUAGE_ID = "kotlin"

val TEST_DATA_PATH = Paths.get("./src/test/testData/").toAbsolutePath().toString()

fun CodeInsightTestFixture.stubComposableAnnotation() {
    addFileToProject(
        "src/androidx/compose/runtime/Composable.kt",
        // language=kotlin
        """
    package androidx.compose.runtime
    @Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.TYPE_USAGE,
        AnnotationTarget.TYPE,
        AnnotationTarget.TYPE_PARAMETER,
        AnnotationTarget.PROPERTY_GETTER
    )
    annotation class Composable
    """
            .trimIndent()
    )
}

fun CodeInsightTestFixture.stubPreviewAnnotation() {
    addFileToProject(
        "/src/androidx/compose/ui/tooling/preview/Preview.kt",
        // language=kotlin
        """
    package androidx.compose.ui.tooling.preview

    import kotlin.reflect.KClass

    object Devices {
        const val DEFAULT = ""

        const val NEXUS_7 = "id:Nexus 7"
        const val NEXUS_10 = "name:Nexus 10"
    }


    @Repeatable
    annotation class Preview(
      val name: String = "",
      val group: String = "",
      val apiLevel: Int = -1,
      val theme: String = "",
      val widthDp: Int = -1,
      val heightDp: Int = -1,
      val locale: String = "",
      val fontScale: Float = 1f,
      val showDecoration: Boolean = false,
      val showBackground: Boolean = false,
      val backgroundColor: Long = 0,
      val uiMode: Int = 0,
      val device: String = ""
    )

    interface PreviewParameterProvider<T> {
        val values: Sequence<T>
        val count get() = values.count()
    }

    annotation class PreviewParameter(
        val provider: KClass<out PreviewParameterProvider<*>>,
        val limit: Int = Int.MAX_VALUE
    )
    """
            .trimIndent()
    )
}

