package com.alefranc.composescreenshotplugin

import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.lang.Language
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.plugins.gradle.settings.GradleProjectSettings
import org.jetbrains.plugins.gradle.settings.GradleSettings

abstract class BaseLineMarkerProviderTestCase : BasePlatformTestCase() {
    abstract val lineMarkerProvider: LineMarkerProvider

    private val language by lazy {
        Language.findLanguageByID(KOTLIN_LANGUAGE_ID)
    }

    override fun getTestDataPath(): String = TEST_DATA_PATH

    override fun setUp() {
        super.setUp()

        if (!isTestInitialized) {
            language?.let {
                LineMarkerProviders.getInstance().clearCache()
                LineMarkerProviders.getInstance().addExplicitExtension(it, lineMarkerProvider)
            }

            GradleSettings.getInstance(project).linkProject(
                GradleProjectSettings().apply {
                    externalProjectPath = project.basePath.orEmpty()
                }
            )

            isTestInitialized = true
        }

        myFixture.stubComposableAnnotation()
        myFixture.stubPreviewAnnotation()
    }

    private companion object {
        private var isTestInitialized = false
    }
}
