package com.alefranc.composescreenshotplugin.utility

import com.android.tools.idea.gradle.project.model.GradleAndroidModel
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

private val COMPOSE_COMPOSABLE_ANNOTATION_CLASS_ID = ClassId.fromString("androidx/compose/runtime/Composable")
private val COMPOSE_PREVIEW_ANNOTATION_CLASS_ID = ClassId.fromString("androidx/compose/ui/tooling/preview/Preview")

private const val SCREENSHOT_TEST_DIRECTORY = "screenshotTest"
private const val PREVIEW_REPORTS = "build/reports/screenshotTest/preview"
private const val SCREENSHOT_TEST_REFERENCE_DIRECTORY = "reference"

const val SCREENSHOT_REFERENCE_FILE_EXTENSION = "png"

val GradleAndroidModel.screenshotReportPath: String
    get() = "$rootDirPath/$PREVIEW_REPORTS/${selectedVariant.pathSegments}/index.html"

val KtClass.isScreenshotTestClassWithComposablePreviewFunction: Boolean
    get() = isScreenshotTestClass && hasComposablePreviewFunction

val KtNamedFunction.isScreenshotTestFunction: Boolean
    get() = getNonStrictParentOfType(KtClass::class.java)?.isScreenshotTestClass == true &&
        hasComposablePreviewAnnotation

fun PsiDirectory.containsScreenshotTestPath(): Boolean {
    val subdirectories = subdirectories
    for (subdirectory in subdirectories) {
        if (subdirectory.isScreenshotTestPath || subdirectory.containsScreenshotTestPath()) {
            return true
        }
    }
    return false
}

fun PsiElement.getReferenceImagesPathRegex(): String? {
    val referencePath = androidModel?.screenshotReferencePath ?: return null

    return getFqName()?.pathSegments()?.joinToString("/")?.let { path ->
        when (this) {
            is KtClass -> "$referencePath/$path/.*"
            is KtNamedFunction -> "$referencePath/${path}_.*"
            is PsiDirectory -> "$referencePath/$path/.*"
            else -> null
        }
    } ?: "$referencePath/.*"
}

private val KtNamedFunction.hasComposableAnnotation: Boolean
    get() = hasAnnotation(COMPOSE_COMPOSABLE_ANNOTATION_CLASS_ID)

private val KtNamedFunction.hasComposePreviewAnnotation: Boolean
    get() = hasAnnotation(COMPOSE_PREVIEW_ANNOTATION_CLASS_ID)

private val KtNamedFunction.hasComposablePreviewAnnotation: Boolean
    get() = hasComposableAnnotation && hasComposePreviewAnnotation

private val KtFile.isScreenshotTestPath: Boolean
    get() = virtualFilePath.contains(SCREENSHOT_TEST_DIRECTORY)

private val PsiDirectory.isScreenshotTestPath: Boolean
    get() = virtualFile.path.contains(SCREENSHOT_TEST_DIRECTORY)

private val GradleAndroidModel.screenshotReferencePath: String
    get() = "$rootDirPath/src/${selectedVariant.name}/$SCREENSHOT_TEST_DIRECTORY/$SCREENSHOT_TEST_REFERENCE_DIRECTORY"

private val KtClass.isScreenshotTestClass: Boolean
    get() = !isData() &&
        !isInterface() &&
        !isEnum() &&
        !isSealed() &&
        !isInner() &&
        containingKtFile.isScreenshotTestPath

private val KtClass.hasComposablePreviewFunction: Boolean
    get() = declarations.filterIsInstance<KtNamedFunction>().any { it.hasComposablePreviewAnnotation }
