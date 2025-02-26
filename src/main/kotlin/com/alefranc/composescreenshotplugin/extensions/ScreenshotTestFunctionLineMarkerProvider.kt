package com.alefranc.composescreenshotplugin.extensions

import com.alefranc.composescreenshotplugin.navHandlers.ScreenshotClassNavHandler
import com.alefranc.composescreenshotplugin.utility.hasComposablePreviewAnnotation
import com.alefranc.composescreenshotplugin.utility.isGradleProject
import com.alefranc.composescreenshotplugin.utility.isScreenshotTestClass
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer.Alignment.RIGHT
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

class ScreenshotTestFunctionLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (!element.project.isGradleProject()) return null
        if (element !is KtNamedFunction ||
            element.getNonStrictParentOfType(KtClass::class.java)?.isScreenshotTestClass != true ||
            !element.hasComposablePreviewAnnotation
        ) {
            return null
        }

        return element.getLineMarkerInfo()
    }

    private fun KtNamedFunction.getLineMarkerInfo(): LineMarkerInfo<PsiElement>? {
        val element = this.nameIdentifier ?: return null

        return LineMarkerInfo(
            element,
            element.textRange,
            AllIcons.RunConfigurations.TestState.Green2,
            { "Run screenshot test" },
            ScreenshotClassNavHandler(this),
            RIGHT,
            { "Run screenshot test" }
        )
    }
}
