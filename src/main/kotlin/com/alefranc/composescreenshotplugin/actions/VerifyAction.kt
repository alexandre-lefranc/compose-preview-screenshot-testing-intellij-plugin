package com.alefranc.composescreenshotplugin.actions

import com.alefranc.composescreenshotplugin.actions.base.BaseScreenshotAction
import com.alefranc.composescreenshotplugin.content.PluginIcons.ICON_ACTION_EXECUTE
import com.alefranc.composescreenshotplugin.content.PluginNotifications.DEFAULT_NOTIFICATION_GROUP_ID
import com.alefranc.composescreenshotplugin.content.PluginTexts.ACTION_TEXT_VERIFY
import com.alefranc.composescreenshotplugin.content.PluginTexts.NOTIFICATION_VERIFY_TEXT
import com.alefranc.composescreenshotplugin.utility.screenshotReportPath
import com.android.tools.idea.gradle.project.model.GradleAndroidModel
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType.INFORMATION
import com.intellij.notification.Notifications.Bus.notify
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

class VerifyAction(
    anchorElement: PsiElement? = null,
) : BaseScreenshotAction(anchorElement) {

    override val actionIcon = ICON_ACTION_EXECUTE
    override val actionText = ACTION_TEXT_VERIFY

    override val gradleCommandLineOption = "tests"
    override val gradleCommandLine = "validate"

    override fun onTaskSuccess(project: Project, androidModel: GradleAndroidModel) {
        val reportPath = androidModel.screenshotReportPath

        project.createAndShowNotification(reportPath)
    }

    private fun Project.createAndShowNotification(reportPath: String) {
        val notification = Notification(
            DEFAULT_NOTIFICATION_GROUP_ID,
            NOTIFICATION_VERIFY_TEXT,
            INFORMATION,
        )

        notification.addAction(ShowReportAction(reportPath))

        notify(notification, this)
    }
}
