package com.alefranc.composescreenshotplugin.actions

import com.alefranc.composescreenshotplugin.actions.base.BaseReferenceFilesAction
import com.alefranc.composescreenshotplugin.content.PluginIcons.ICON_ACTION_FIND
import com.alefranc.composescreenshotplugin.content.PluginTexts.ACTION_TEXT_SHOW_REFERENCES
import com.alefranc.composescreenshotplugin.utility.openFilesInRightSplit
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement

class ShowReferencesAction(
    anchorElement: PsiElement? = null,
) : BaseReferenceFilesAction(anchorElement) {

    override val actionIcon = ICON_ACTION_FIND
    override val actionText = ACTION_TEXT_SHOW_REFERENCES

    override fun onActionPerformed(project: Project, references: List<VirtualFile>) {
        FileEditorManagerEx.getInstanceEx(project).openFilesInRightSplit(project, references)
    }
}
