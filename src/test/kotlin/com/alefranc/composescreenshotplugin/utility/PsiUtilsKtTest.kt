package com.alefranc.composescreenshotplugin.utility

import com.intellij.openapi.application.runWriteAction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.utils.vfs.getPsiFile
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

class PsiUtilsKtTest : BasePlatformTestCase() {
    fun `test GIVEN a KtClass WHEN calling PsiElement_getFqName() THEN make sure the fq name is correct`() {
        // GIVEN
        val kotlinFile = myFixture.configureByText(
            "Test.kt",
            """
            package com.example
            class MyClass
            """.trimIndent()
        ) as KtFile

        val given = kotlinFile.declarations.filterIsInstance<KtClass>().firstOrNull() as? PsiElement

        // WHEN
        val actual = given?.getFqName()

        // THEN
        assertEquals(FqName("com.example.MyClass"), actual)
    }

    fun `test GIVEN a KtNamedFunction WHEN calling PsiElement_getFqName() THEN make sure the fq name is correct`() {
        // GIVEN
        val kotlinFile = myFixture.configureByText(
            "Test.kt",
            """
            package com.example
            class MyClass {
                fun foo(): {}
            }
            """.trimIndent()
        ) as KtFile

        val ktClass = kotlinFile.declarations.filterIsInstance<KtClass>().firstOrNull()
        val given = ktClass?.declarations?.filterIsInstance<KtNamedFunction>()?.firstOrNull() as? PsiElement

        // WHEN
        val actual = given?.getFqName()

        // THEN
        assertEquals(FqName("com.example.MyClass.foo"), actual)
    }

    fun `test GIVEN a PsiDirectory WHEN calling PsiElement_getFqName() THEN make sure the fq name is correct`() {
        // GIVEN
        val exampleDir = runWriteAction {
            val projectBase = myFixture.tempDirFixture.findOrCreateDir("src")
            val comDir = projectBase.createChildDirectory(this, "com")
            comDir.createChildDirectory(this, "example")
        }

        val given = PsiManager.getInstance(project).findDirectory(exampleDir) as? PsiElement

        // WHEN
        val actual = given?.getFqName()

        // THEN
        assertEquals(FqName("src.com.example"), actual)
    }

    fun `test GIVEN a PsiFile WHEN calling PsiElement_getFqName() THEN make sure the fq name is null`() {
        // GIVEN
        val given = LightVirtualFile("Test.kt", KotlinLanguage.INSTANCE, "").getPsiFile(project)

        // WHEN
        val actual = given.getFqName()

        // THEN
        assertEquals(null, actual)
    }
}
