package com.alefranc.composescreenshotplugin.utility

import com.alefranc.composescreenshotplugin.content.PluginTexts.ERROR_NO_FILES_FOUND
import com.alefranc.composescreenshotplugin.content.PluginTexts.ERROR_TITLE
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope

fun FileEditorManagerEx.openFilesInRightSplit(project: Project, files: List<VirtualFile>) {
    if (files.isEmpty()) {
        Messages.showMessageDialog(project, ERROR_NO_FILES_FOUND, ERROR_TITLE, Messages.getErrorIcon())
        return
    }

    currentWindow?.split(1, true, files.first(), true)

    files.drop(1).forEach { file ->
        openFile(file, false)
    }
}

fun Project.findFilesByExtension(extension: String, pathRegex: String?): List<VirtualFile> {
    val fileType = FileTypeManager.getInstance().getFileTypeByExtension(extension)
    val pngFiles = FileTypeIndex.getFiles(fileType, GlobalSearchScope.allScope(this))
    return pngFiles.filter { pathRegex == null || it.path.matches(pathRegex.toRegex()) }
}
