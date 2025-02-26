package com.alefranc.composescreenshotplugin.extensions

import com.alefranc.composescreenshotplugin.navHandlers.ScreenshotClassNavHandler
import com.alefranc.composescreenshotplugin.utility.PluginTexts.RunScreenshotTests
import com.alefranc.composescreenshotplugin.utility.isGradleProject
import com.alefranc.composescreenshotplugin.utility.isScreenshotTestClassWithComposablePreviewFunction
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer.Alignment.RIGHT
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass

class ScreenshotTestClassLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (!element.project.isGradleProject() ||
            element !is KtClass ||
            !element.isScreenshotTestClassWithComposablePreviewFunction
        ) {
            return null
        }

        return element.getLineMarkerInfo()
    }

    private fun KtClass.getLineMarkerInfo(): LineMarkerInfo<PsiElement>? {
        val element = this.nameIdentifier ?: return null

        return LineMarkerInfo(
            element,
            element.textRange,
            AllIcons.RunConfigurations.TestState.Green2,
            { RunScreenshotTests },
            ScreenshotClassNavHandler(this),
            RIGHT,
            { RunScreenshotTests }
        )
    }
}
