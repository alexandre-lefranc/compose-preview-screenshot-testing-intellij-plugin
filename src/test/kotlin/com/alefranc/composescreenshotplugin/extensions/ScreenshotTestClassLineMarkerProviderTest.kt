package com.alefranc.composescreenshotplugin.extensions

import com.alefranc.composescreenshotplugin.BaseLineMarkerProviderTestCase
import com.intellij.codeInsight.daemon.LineMarkerInfo.LineMarkerGutterIconRenderer
import com.intellij.icons.AllIcons.RunConfigurations.TestState.Green2

@Suppress("MaxLineLength")
class ScreenshotTestClassLineMarkerProviderTest : BaseLineMarkerProviderTestCase() {

    override val lineMarkerProvider = ScreenshotTestClassLineMarkerProvider()

    fun `test GIVEN a class without screenshot test WHEN finding all gutters THEN check that there is no gutter`() {
        val file = myFixture.copyFileToProject(
            "NoScreenshotTestClass.kt",
            "src/screenshotTest/kotlin/com/myapp/NoScreenshotTestClass.kt",
        )
        myFixture.configureFromExistingVirtualFile(file)
        checkGutters(emptyList())
    }

    fun `test GIVEN a kotlin file without screenshot test WHEN finding all gutters THEN check that there is no gutter`() {
        val file = myFixture.copyFileToProject(
            "UtilityClass.kt",
            "src/screenshotTest/kotlin/com/myapp/UtilityClass.kt",
        )
        myFixture.configureFromExistingVirtualFile(file)
        checkGutters(emptyList())
    }

    fun `test GIVEN a screenshot test class inside another folder WHEN finding all gutters THEN check that there is one gutter for the screenshot test`() {
        val file = myFixture.copyFileToProject(
            "SingleScreenshotTestClass.kt",
            "src/kotlin/com/myapp/SingleScreenshotTestClass.kt",
        )
        myFixture.configureFromExistingVirtualFile(file)
        checkGutters(emptyList())
    }

    fun `test GIVEN a single screenshot test class WHEN finding all gutters THEN check that there is one gutter for the screenshot test`() {
        val file = myFixture.copyFileToProject(
            "SingleScreenshotTestClass.kt",
            "src/screenshotTest/kotlin/com/myapp/SingleScreenshotTestClass.kt",
        )
        myFixture.configureFromExistingVirtualFile(file)
        checkGutters(listOf(157))
    }

    fun `test GIVEN a class with many screenshot tests WHEN finding all gutters THEN check that there is one gutter for screenshot tests`() {
        val file = myFixture.copyFileToProject(
            "ManyScreenshotTestsClass.kt",
            "src/screenshotTest/kotlin/com/myapp/ManyScreenshotTestsClass.kt",
        )
        myFixture.configureFromExistingVirtualFile(file)
        checkGutters(listOf(250))
    }

    private fun checkGutters(offsets: List<Int>) {
        myFixture.doHighlighting()

        val gutters = myFixture
            .findAllGutters()
            .filterIsInstance<LineMarkerGutterIconRenderer<*>>()
            .sortedBy { it.lineMarkerInfo.startOffset }

        assertEquals(offsets.size, gutters.size)

        gutters.forEachIndexed { index, gutter ->
            assertEquals("Run screenshot test(s)", gutter.tooltipText)
            assertEquals(Green2, gutter.icon)
            assertEquals(offsets[index], gutter.lineMarkerInfo.startOffset)
            assertNotNull(gutter.clickAction)
        }
    }
}
