package com.alefranc.composescreenshotplugin.actions.base

import com.alefranc.composescreenshotplugin.utility.SCREENSHOT_REFERENCE_FILE_EXTENSION
import com.alefranc.composescreenshotplugin.utility.androidModel
import com.alefranc.composescreenshotplugin.utility.findFilesByExtension
import com.alefranc.composescreenshotplugin.utility.getFqName
import com.alefranc.composescreenshotplugin.utility.screenshotReferencePath
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

abstract class BaseReferenceFilesAction(
    targetElement: PsiElement? = null,
) : BaseAction(targetElement) {

    abstract fun onActionPerformed(project: Project, references: List<VirtualFile>)

    override fun actionPerformed(actionEvent: AnActionEvent) {
        val project = actionEvent.project ?: return
        val pathRegex = getElement(actionEvent)?.getReferenceImagesPath() ?: return
        val files = project.findFilesByExtension(SCREENSHOT_REFERENCE_FILE_EXTENSION, pathRegex)

        onActionPerformed(project, files)
    }

    override fun update(actionEvent: AnActionEvent) {
        actionEvent.presentation.apply {
            text = actionText
            icon = actionIcon
        }
    }

    private fun PsiElement.getReferenceImagesPath(): String? {
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
}
