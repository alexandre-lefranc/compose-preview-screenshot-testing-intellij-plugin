package com.alefranc.composescreenshotplugin.utility

import com.alefranc.composescreenshotplugin.TEST_DATA_PATH
import com.alefranc.composescreenshotplugin.stubComposableAnnotation
import com.alefranc.composescreenshotplugin.stubPreviewAnnotation
import com.intellij.psi.PsiFile
import com.intellij.testFramework.TestDataFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class IsScreenshotTestClassWithComposablePreviewFunctionTest : BasePlatformTestCase() {

    @Parameter
    @JvmField
    var filePath: String = ""

    @Parameter(1)
    @JvmField
    var expected: Boolean? = null

    override fun getTestDataPath(): String = TEST_DATA_PATH

    override fun setUp() {
        super.setUp()

        myFixture.stubComposableAnnotation()
        myFixture.stubPreviewAnnotation()
    }

    @Test
    fun test() {
        // GIVEN
        val kotlinFile = setupFile(filePath) as? KtFile

        val given = kotlinFile?.declarations?.filterIsInstance<KtClass>()?.firstOrNull()

        // WHEN
        val actual = given?.isScreenshotTestClassWithComposablePreviewFunction

        // THEN
        assertEquals(expected, actual)
    }

    private fun setupFile(@TestDataFile sourceFilePath: String): PsiFile? {
        val file = myFixture.copyFileToProject(sourceFilePath)

        myFixture.configureFromExistingVirtualFile(file)

        return file.toPsiFile(project)
    }

    companion object {
        @JvmStatic
        @Parameters(name = "GIVEN {0} in {1} WHEN checking THEN should be {2}")
        fun params() = arrayOf(
            arrayOf(
                "src/main/ClassWithComposablePreview.kt",
                false,
            ),
            arrayOf(
                "src/screenshotTest/ClassWithManyScreenshotTests.kt",
                true,
            ),
            arrayOf(
                "src/screenshotTest/ClassWithoutScreenshotTest.kt",
                false,
            ),
            arrayOf(
                "src/screenshotTest/ClassWithSingleScreenshotTest.kt",
                true,
            ),
            arrayOf(
                "src/screenshotTest/ComposablePreviewFunctions.kt",
                null,
            ),
        )
    }
}
