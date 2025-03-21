package com.alefranc.composescreenshotplugin.extensions

import com.alefranc.composescreenshotplugin.content.PluginIcons.ICON_GUTTER_RUN
import com.alefranc.composescreenshotplugin.content.PluginTexts.GUTTER_RUN_SCREENSHOT_TEST
import com.alefranc.composescreenshotplugin.navHandlers.ScreenshotClassNavHandler
import com.alefranc.composescreenshotplugin.utility.isGradleProject
import com.alefranc.composescreenshotplugin.utility.isScreenshotTestFunction
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer.Alignment.RIGHT
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtNamedFunction

class ScreenshotTestFunctionLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (!element.project.isGradleProject) return null
        if (element !is KtNamedFunction || !element.isScreenshotTestFunction) return null

        return element.getLineMarkerInfo()
    }

    private fun KtNamedFunction.getLineMarkerInfo(): LineMarkerInfo<PsiElement>? {
        val element = this.nameIdentifier ?: return null

        return LineMarkerInfo(
            element,
            element.textRange,
            ICON_GUTTER_RUN,
            { GUTTER_RUN_SCREENSHOT_TEST },
            ScreenshotClassNavHandler(this),
            RIGHT,
            { GUTTER_RUN_SCREENSHOT_TEST }
        )
    }
}
