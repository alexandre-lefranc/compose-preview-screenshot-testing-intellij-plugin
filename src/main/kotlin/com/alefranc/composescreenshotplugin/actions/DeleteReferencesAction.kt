package com.alefranc.composescreenshotplugin.actions

import com.alefranc.composescreenshotplugin.actions.base.BaseReferenceFilesAction
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import java.io.IOException


class DeleteReferencesAction(
    anchorElement: PsiElement? = null,
) : BaseReferenceFilesAction(anchorElement) {

    override val actionIcon = AllIcons.General.Delete
    override val actionText = "Delete references images"

    override fun onActionPerformed(project: Project, references: List<VirtualFile>) {
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                try {
                    references.forEach { it.delete(it) }
                } catch (e: IOException) {

                }
            }
        }
    }
}
