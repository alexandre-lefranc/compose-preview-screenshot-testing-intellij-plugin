package com.alefranc.composescreenshotplugin.extensions

import com.alefranc.composescreenshotplugin.testUtils.KOTLIN_LANGUAGE_ID
import com.alefranc.composescreenshotplugin.testUtils.stubComposableAnnotation
import com.alefranc.composescreenshotplugin.testUtils.stubPreviewAnnotation
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.icons.AllIcons
import com.intellij.lang.Language
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.nio.file.Paths

class ScreenshotClassLineMarkerProviderTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()

        Language.findLanguageByID(KOTLIN_LANGUAGE_ID)?.let { language ->
            LineMarkerProviders.getInstance().clearCache(language)
            LineMarkerProviders.getInstance().addExplicitExtension(language, ScreenshotClassLineMarkerProvider())
        }

        myFixture.stubComposableAnnotation()
        myFixture.stubPreviewAnnotation()
        myFixture.testDataPath = Paths.get("./src/test/testData/").toAbsolutePath().toString()
        val file = myFixture.copyFileToProject(
            "SingleScreenshotTestClass.kt",
            "src/screenshotTest/kotlin/com/myapp/SingleScreenshotTestClass.kt",
        )
        myFixture.configureFromExistingVirtualFile(file)
    }

    fun testValidScreenshotTestFile() {
        val gutters = myFixture.findAllGutters()

        assertEquals(1, gutters.size)
        assertEquals(gutters[0].tooltipText, "Run screenshot test(s)")
        assertEquals((gutters[0] as LineMarkerInfo.LineMarkerGutterIconRenderer<*>).lineMarkerInfo.startOffset, 157)
        assertEquals(gutters[0].icon, AllIcons.RunConfigurations.TestState.Green2)
    }
}
