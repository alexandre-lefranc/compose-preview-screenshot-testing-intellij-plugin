package com.alefranc.composescreenshotplugin.utility

import com.android.tools.idea.gradle.model.IdeVariant
import com.android.tools.idea.gradle.project.model.GradleAndroidModel
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlinx.serialization.compiler.resolve.toClassDescriptor

private val COMPOSE_ANNOTATION_FQ_NAME = FqName("androidx.compose.runtime.Composable")

private val COMPOSE_PREVIEW_ANNOTATION_FQ_NAME = FqName("androidx.compose.ui.tooling.preview.Preview")

const val SCREENSHOT_TEST_DIRECTORY = "screenshotTest"

private const val PREVIEW_REPORTS = "build/reports/screenshotTest/preview"

const val SCREENSHOT_TEST_REFERENCE_DIRECTORY = "reference"

const val SCREENSHOT_REFERENCE_FILE_EXTENSION = "png"

val GradleAndroidModel.screenshotReportPath: String
    get() = "$rootDirPath/$PREVIEW_REPORTS/${selectedVariant.computePathSegments()}/index.html"

val GradleAndroidModel.screenshotReferencePath: String
    get() = "$rootDirPath/src/${selectedVariant.name}/$SCREENSHOT_TEST_DIRECTORY/$SCREENSHOT_TEST_REFERENCE_DIRECTORY"

val KtClass.isScreenshotTestClassWithComposablePreviewFunction: Boolean
    get() = this.isScreenshotTestClass && this.hasComposablePreviewFunction

val KtClass.isScreenshotTestClass: Boolean
    get() = !this.isData() &&
        !this.isInterface() &&
        !this.isEnum() &&
        !this.isSealed() &&
        !this.isInner() &&
        containingKtFile.isScreenshotTestPath

val KtClass.hasComposablePreviewFunction: Boolean
    get() {
        val functions = PsiTreeUtil.findChildrenOfType(this, KtNamedFunction::class.java)

        return functions.any { it.hasComposablePreviewAnnotation }
    }

val KtNamedFunction.hasComposablePreviewAnnotation: Boolean
    get() = this.hasComposableAnnotation && this.hasComposePreviewAnnotation

val KtFile.isScreenshotTestPath: Boolean
    get() = virtualFilePath.contains(SCREENSHOT_TEST_DIRECTORY)

val PsiDirectory.isScreenshotTestPath: Boolean
    get() = virtualFile.path.contains(SCREENSHOT_TEST_DIRECTORY)

fun PsiElement.getReferenceImagesPath(): String? {
    val referencePath = this.androidModel?.screenshotReferencePath ?: return null

    return getFqName()?.pathSegments()?.joinToString("/")?.let { path ->
        when (this) {
            is KtClass -> "$referencePath/$path/.*"
            is KtNamedFunction -> "$referencePath/${path}_.*"
            is PsiDirectory -> "$referencePath/$path/.*"
            else -> null
        }
    }
}

private fun IdeVariant.computePathSegments(): String {
    return buildType + "/" + productFlavors.capitalizeExceptFirst().joinToString("")
}

private val KtNamedFunction.hasComposableAnnotation: Boolean
    get() = this.descriptor?.annotations?.hasAnnotation(COMPOSE_ANNOTATION_FQ_NAME) == true

private val KtNamedFunction.hasComposePreviewAnnotation: Boolean
    get() {
        return this.descriptor?.annotations?.hasAnnotation(COMPOSE_PREVIEW_ANNOTATION_FQ_NAME) == true ||
            this.descriptor?.annotations?.any {
                it.type.toClassDescriptor?.annotations?.hasAnnotation(COMPOSE_PREVIEW_ANNOTATION_FQ_NAME) == true
            } == true
    }
