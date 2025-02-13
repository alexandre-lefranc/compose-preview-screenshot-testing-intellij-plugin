package com.alefranc.composescreenshotplugin.actions

import com.alefranc.composescreenshotplugin.actions.base.BaseScreenshotAction
import com.alefranc.composescreenshotplugin.utility.screenshotReportPath
import com.android.tools.idea.gradle.project.model.GradleAndroidModel
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import java.awt.Desktop
import java.net.URI

class VerifyAction(
    anchorElement: PsiElement? = null,
) : BaseScreenshotAction(anchorElement) {

    override val actionIcon = AllIcons.Actions.Execute
    override val actionText = "Verify"

    override val gradleCommandLineOption = "tests"
    override val gradleCommandLine = "validate"

    @Suppress("TooGenericExceptionCaught")
    override fun onTaskSuccess(project: Project, androidModel: GradleAndroidModel) {
        val reportPath = androidModel.screenshotReportPath

        ApplicationManager.getApplication().invokeLater {
            try {
                Desktop.getDesktop().browse(URI.create("file://$reportPath"))
            } catch (exception: Exception) {
                Messages.showMessageDialog(
                    project,
                    "Cannot open report at $reportPath",
                    "Error",
                    Messages.getErrorIcon()
                )
            }
        }
    }
}
