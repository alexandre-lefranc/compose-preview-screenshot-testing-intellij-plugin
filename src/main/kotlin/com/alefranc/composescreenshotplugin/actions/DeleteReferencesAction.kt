package com.alefranc.composescreenshotplugin.actions

import com.alefranc.composescreenshotplugin.actions.base.BaseReferenceFilesAction
import com.alefranc.composescreenshotplugin.content.PluginIcons.ICON_ACTION_DELETE
import com.alefranc.composescreenshotplugin.content.PluginNotifications.DEFAULT_NOTIFICATION_GROUP_ID
import com.alefranc.composescreenshotplugin.content.PluginTexts.ACTION_TEXT_DELETE_REFERENCES
import com.alefranc.composescreenshotplugin.content.PluginTexts.CONFIRMATION_DELETE_MESSAGE
import com.alefranc.composescreenshotplugin.content.PluginTexts.CONFIRMATION_DELETE_TITLE
import com.alefranc.composescreenshotplugin.content.PluginTexts.NOTIFICATION_IMAGES_DELETED_TEXT
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType.INFORMATION
import com.intellij.notification.Notifications.Bus.notify
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages.YES
import com.intellij.openapi.ui.Messages.getQuestionIcon
import com.intellij.openapi.ui.Messages.showYesNoDialog
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import java.io.IOException

class DeleteReferencesAction(
    anchorElement: PsiElement? = null,
) : BaseReferenceFilesAction(anchorElement) {

    override val actionIcon = ICON_ACTION_DELETE
    override val actionText = ACTION_TEXT_DELETE_REFERENCES

    override fun onActionPerformed(project: Project, references: List<VirtualFile>) {
        val result = showYesNoDialog(
            project,
            CONFIRMATION_DELETE_MESSAGE,
            CONFIRMATION_DELETE_TITLE,
            getQuestionIcon(),
        )

        if (result == YES) {
            deleteFiles(project, references)
        }
    }

    private fun deleteFiles(project: Project, references: List<VirtualFile>) {
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                try {
                    references.forEach { it.delete(project) }
                    val notification = Notification(
                        DEFAULT_NOTIFICATION_GROUP_ID,
                        NOTIFICATION_IMAGES_DELETED_TEXT,
                        INFORMATION,
                    )

                    notify(notification, project)
                } catch (exception: IOException) {
                    logger.error("Error while deleting file", exception)
                }
            }
        }
    }
}
