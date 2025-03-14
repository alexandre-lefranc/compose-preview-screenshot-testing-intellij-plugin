package com.alefranc.composescreenshotplugin.actions

import com.alefranc.composescreenshotplugin.utility.androidModel
import com.alefranc.composescreenshotplugin.utility.containsScreenshotTestPath
import com.alefranc.composescreenshotplugin.utility.isScreenshotTestClassWithComposablePreviewFunction
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
        val psiElement = actionEvent.getData(PSI_ELEMENT)

        if (project == null || psiElement?.androidModel == null) {
            // If no project defined, disable the menu item
            presentation.isEnabledAndVisible = false
            return
        }

        if (psiElement is PsiDirectory && psiElement.containsScreenshotTestPath()) {
            presentation.isEnabledAndVisible = true
        } else if (psiElement is KtClass && psiElement.isScreenshotTestClassWithComposablePreviewFunction) {
            presentation.isEnabledAndVisible = true
        } else {
            presentation.isEnabledAndVisible = false
        }
    }
}
