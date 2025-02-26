package com.alefranc.composescreenshotplugin.extensions

import com.alefranc.composescreenshotplugin.KOTLIN_LANGUAGE_ID
import com.alefranc.composescreenshotplugin.TEST_DATA_PATH
import com.alefranc.composescreenshotplugin.stubComposableAnnotation
import com.alefranc.composescreenshotplugin.stubPreviewAnnotation
import com.alefranc.composescreenshotplugin.utility.PluginTexts.RunScreenshotTest
import com.alefranc.composescreenshotplugin.utility.PluginTexts.RunScreenshotTests
import com.intellij.codeInsight.daemon.LineMarkerInfo.LineMarkerGutterIconRenderer
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.icons.AllIcons.RunConfigurations.TestState.Green2
import com.intellij.lang.Language
import com.intellij.testFramework.TestDataFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.plugins.gradle.settings.GradleProjectSettings
import org.jetbrains.plugins.gradle.settings.GradleSettings
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class ScreenshotTestLineMarkersTestCase : BasePlatformTestCase() {
    @Parameter
    @JvmField
    var filePath: String = ""

    @Parameter(1)
    @JvmField
    var expected: List<Pair<String, Int>> = emptyList()

    private val language by lazy {
        Language.findLanguageByID(KOTLIN_LANGUAGE_ID)
    }

    override fun getTestDataPath(): String = TEST_DATA_PATH

    override fun setUp() {
        super.setUp()

        val gradleSettings = GradleSettings.getInstance(project)

        if (gradleSettings.linkedProjectsSettings.none { it.externalProjectPath == project.basePath.orEmpty() }) {
            language?.let {
                LineMarkerProviders.getInstance().clearCache()
                LineMarkerProviders.getInstance().addExplicitExtension(it, ScreenshotTestClassLineMarkerProvider())
                LineMarkerProviders.getInstance().addExplicitExtension(it, ScreenshotTestFunctionLineMarkerProvider())
            }

            gradleSettings.linkProject(
                GradleProjectSettings().apply {
                    externalProjectPath = project.basePath.orEmpty()
                }
            )
        }

        myFixture.stubComposableAnnotation()
        myFixture.stubPreviewAnnotation()
    }

    override fun tearDown() {
        try {
            language?.let { LineMarkerProviders.getInstance().clearCache(it) }
        } finally {
            super.tearDown()
        }
    }

    @Test
    fun test() {
        setupFile(filePath)

        myFixture.doHighlighting()

        val gutters = myFixture
            .findAllGutters()
            .filterIsInstance<LineMarkerGutterIconRenderer<*>>()
            .sortedBy { it.lineMarkerInfo.startOffset }

        assertEquals(expected.size, gutters.size)

        gutters.forEachIndexed { index, gutter ->
            assertEquals(expected[index].first, gutter.tooltipText)
            assertEquals(Green2, gutter.icon)
            assertEquals(expected[index].second, gutter.lineMarkerInfo.startOffset)
            assertNotNull(gutter.clickAction)
        }
    }

    private fun setupFile(@TestDataFile sourceFilePath: String) {
        val file = myFixture.copyFileToProject(sourceFilePath)
        myFixture.configureFromExistingVirtualFile(file)
    }

    companion object {
        @JvmStatic
        @Parameters(name = "GIVEN {0} in {1} WHEN checking THEN should be {2}")
        fun params() = arrayOf(
            arrayOf(
                "src/main/ClassWithComposablePreview.kt",
                emptyList<Pair<String, Int>>(),
            ),
            arrayOf(
                "src/screenshotTest/ClassWithManyScreenshotTests.kt",
                listOf(
                    RunScreenshotTests to 157,
                    RunScreenshotTest to 221,
                    RunScreenshotTest to 332,
                    RunScreenshotTest to 450,
                ),
            ),
            arrayOf(
                "src/screenshotTest/ClassWithoutScreenshotTest.kt",
                emptyList<Pair<String, Int>>(),
            ),
            arrayOf(
                "src/screenshotTest/ClassWithSingleScreenshotTest.kt",
                listOf(
                    RunScreenshotTests to 157,
                    RunScreenshotTest to 226,
                ),
            ),
            arrayOf(
                "src/screenshotTest/ComposablePreviewFunctions.kt",
                emptyList<Pair<String, Int>>(),
            ),
        )
    }
}
