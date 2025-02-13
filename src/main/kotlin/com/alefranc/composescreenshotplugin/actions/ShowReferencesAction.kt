package com.alefranc.composescreenshotplugin.actions

import com.alefranc.composescreenshotplugin.actions.base.BaseReferenceFilesAction
import com.alefranc.composescreenshotplugin.utility.openFilesInRightSplit
import com.intellij.icons.AllIcons
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement

class ShowReferencesAction(
    anchorElement: PsiElement? = null,
) : BaseReferenceFilesAction(anchorElement) {

    override val actionIcon = AllIcons.Actions.Find
    override val actionText = "Show references images"

    override fun onActionPerformed(project: Project, references: List<VirtualFile>) {
        FileEditorManagerEx.getInstanceEx(project).openFilesInRightSplit(project, references)
    }
}
