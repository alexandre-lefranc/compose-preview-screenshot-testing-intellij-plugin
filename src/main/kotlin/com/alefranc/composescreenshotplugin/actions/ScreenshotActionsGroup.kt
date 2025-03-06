package com.alefranc.composescreenshotplugin.actions

import com.alefranc.composescreenshotplugin.utility.isScreenshotTestClassWithComposablePreviewFunction
import com.alefranc.composescreenshotplugin.utility.isScreenshotTestPath
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.ActionUpdateThread.BGT
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys.PSI_ELEMENT
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.psi.KtClass

class ScreenshotActionsGroup : DefaultActionGroup() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return BGT
    }

    override fun update(actionEvent: AnActionEvent) {
        val project = actionEvent.project
        val presentation = actionEvent.presentation

        if (project == null) {
            // If no project defined, disable the menu item
            presentation.isEnabledAndVisible = false
            return
        }

        val psiElement = actionEvent.getData(PSI_ELEMENT)
        if (psiElement is PsiDirectory && psiElement.isScreenshotTestPath) {
            presentation.isEnabledAndVisible = true
        } else if (psiElement is KtClass && psiElement.isScreenshotTestClassWithComposablePreviewFunction) {
            presentation.isEnabledAndVisible = true
        } else {
            presentation.isEnabledAndVisible = false
        }
    }
}
