package com.alefranc.composescreenshotplugin.actions

import com.alefranc.composescreenshotplugin.actions.base.BaseReferenceFilesAction
import com.alefranc.composescreenshotplugin.content.PluginIcons.ICON_ACTION_DELETE
import com.alefranc.composescreenshotplugin.content.PluginTexts.ACTION_TEXT_DELETE_REFERENCES
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import java.io.IOException

class DeleteReferencesAction(
    anchorElement: PsiElement? = null,
) : BaseReferenceFilesAction(anchorElement) {

    override val actionIcon = ICON_ACTION_DELETE
    override val actionText = ACTION_TEXT_DELETE_REFERENCES

    override fun onActionPerformed(project: Project, references: List<VirtualFile>) {
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                try {
                    references.forEach { it.delete(project) }
                } catch (exception: IOException) {
                    exception.printStackTrace()
                }
            }
        }
    }
}
