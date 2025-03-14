package com.alefranc.composescreenshotplugin.actions.base

import com.alefranc.composescreenshotplugin.utility.SCREENSHOT_REFERENCE_FILE_EXTENSION
import com.alefranc.composescreenshotplugin.utility.findFilesByExtension
import com.alefranc.composescreenshotplugin.utility.getReferenceImagesPathRegex
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement

abstract class BaseReferenceFilesAction(
    targetElement: PsiElement? = null,
) : BaseAction(targetElement) {

    protected abstract fun onActionPerformed(project: Project, references: List<VirtualFile>)

    override fun actionPerformed(actionEvent: AnActionEvent) {
        val project = actionEvent.project ?: return
        val pathRegex = getElement(actionEvent)?.getReferenceImagesPathRegex() ?: return
        val files = project.findFilesByExtension(SCREENSHOT_REFERENCE_FILE_EXTENSION, pathRegex)

        onActionPerformed(project, files)
    }

    override fun update(actionEvent: AnActionEvent) {
        actionEvent.presentation.apply {
            text = actionText
            icon = actionIcon
        }
    }
}
