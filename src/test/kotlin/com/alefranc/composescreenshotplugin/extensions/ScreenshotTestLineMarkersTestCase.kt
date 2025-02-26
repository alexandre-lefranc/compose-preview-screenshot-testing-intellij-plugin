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

@Suppress("MaxLineLength")
class ScreenshotTestLineMarkersTestCase : BasePlatformTestCase() {
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

    fun `test GIVEN a class without screenshot test WHEN finding all gutters THEN check that there is no gutter`() {
        setupFile(
            sourceFilePath = "NoScreenshotTestClass.kt",
            targetPath = "src/screenshotTest/kotlin/com/myapp/NoScreenshotTestClass.kt",
        )
        checkGutters(emptyList())
    }

    fun `test GIVEN a kotlin file without screenshot test WHEN finding all gutters THEN check that there is no gutter`() {
        setupFile(
            sourceFilePath = "UtilityClass.kt",
            targetPath = "src/screenshotTest/kotlin/com/myapp/UtilityClass.kt",
        )
        checkGutters(emptyList())
    }

    fun `test GIVEN a screenshot test class inside another folder WHEN finding all gutters THEN check that there is one gutter for the screenshot test`() {
        setupFile(
            sourceFilePath = "SingleScreenshotTestClass.kt",
            targetPath = "src/kotlin/com/myapp/SingleScreenshotTestClass.kt",
        )
        checkGutters(emptyList())
    }

    fun `test GIVEN a single screenshot test class WHEN finding all gutters THEN check that there is one gutter for the screenshot test`() {
        setupFile(
            sourceFilePath = "SingleScreenshotTestClass.kt",
            targetPath = "src/screenshotTest/kotlin/com/myapp/SingleScreenshotTestClass.kt",
        )
        checkGutters(
            listOf(
                RunScreenshotTests to 157,
                RunScreenshotTest to 222,
            )
        )
    }

    fun `test GIVEN a class with many screenshot tests WHEN finding all gutters THEN check that there is one gutter for screenshot tests`() {
        setupFile(
            sourceFilePath = "ManyScreenshotTestsClass.kt",
            targetPath = "src/screenshotTest/kotlin/com/myapp/ManyScreenshotTestsClass.kt",
        )
        checkGutters(
            listOf(
                RunScreenshotTests to 250,
                RunScreenshotTest to 314,
                RunScreenshotTest to 425,
                RunScreenshotTest to 543,
            )
        )
    }

    private fun setupFile(@TestDataFile sourceFilePath: String, targetPath: String) {
        val file = myFixture.copyFileToProject(sourceFilePath, targetPath)
        myFixture.configureFromExistingVirtualFile(file)
    }

    private fun checkGutters(infos: List<Pair<String, Int>>) {
        myFixture.doHighlighting()

        val gutters = myFixture
            .findAllGutters()
            .filterIsInstance<LineMarkerGutterIconRenderer<*>>()
            .sortedBy { it.lineMarkerInfo.startOffset }

        assertEquals(infos.size, gutters.size)

        gutters.forEachIndexed { index, gutter ->
            assertEquals(infos[index].first, gutter.tooltipText)
            assertEquals(Green2, gutter.icon)
            assertEquals(infos[index].second, gutter.lineMarkerInfo.startOffset)
            assertNotNull(gutter.clickAction)
        }
    }
}
