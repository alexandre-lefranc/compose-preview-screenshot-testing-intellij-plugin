package com.alefranc.composescreenshotplugin.extensions

import com.alefranc.composescreenshotplugin.testUtils.stubComposableAnnotation
import com.alefranc.composescreenshotplugin.testUtils.stubPreviewAnnotation
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.icons.AllIcons
import com.intellij.lang.Language
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class ScreenshotClassLineMarkerProviderTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()

        Language.findLanguageByID("kotlin")?.let { language ->
            LineMarkerProviders.getInstance().clearCache(language)
            LineMarkerProviders.getInstance().addExplicitExtension(language, ScreenshotClassLineMarkerProvider())
        }

        myFixture.stubComposableAnnotation()
        myFixture.stubPreviewAnnotation()
    }

    fun testValidScreenshotTestFile() {
        val testFile = myFixture.addFileToProject(
            "src/screenshotTest/kotlin/com/myapp/Test.kt",
            """
            package com.myapp

            import androidx.compose.runtime.Composable
            import androidx.compose.ui.tooling.preview.Preview
            import com.myapp.ui.theme.MyAppTheme

            class ExamplePreviewsScreenshots {
                    @Preview
                    @Composable
                    fun Preview() {
                        MyAppTheme {
                            Text("Hello")
                        }
                    }
                }
            """.trimIndent()
        ).virtualFile

        myFixture.configureFromExistingVirtualFile(testFile)

        myFixture.doHighlighting()

        val gutters = myFixture.findAllGutters()

        assertEquals(1, gutters.size)
        assertEquals(gutters[0].tooltipText, "Run screenshot test(s)")
        assertEquals((gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset, 157)
        assertEquals(gutters[0].icon, AllIcons.RunConfigurations.TestState.Green2)
    }
}
