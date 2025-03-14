package com.alefranc.composescreenshotplugin.actions

import com.alefranc.composescreenshotplugin.content.PluginTexts.ACTION_TEXT_OPEN_REPORT
import com.alefranc.composescreenshotplugin.content.PluginTexts.ERROR_CANNOT_OPEN_REPORT
import com.alefranc.composescreenshotplugin.content.PluginTexts.ERROR_NO_MODULE_SELECTED
import com.alefranc.composescreenshotplugin.content.PluginTexts.ERROR_TITLE
import com.alefranc.composescreenshotplugin.utility.screenshotReportPath
import com.android.tools.idea.gradle.project.model.GradleAndroidModel
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys.PSI_ELEMENT
import com.intellij.openapi.actionSystem.LangDataKeys.MODULE_CONTEXT
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.Messages.getErrorIcon
import org.jetbrains.kotlin.idea.base.util.module
import java.awt.Desktop
import java.io.IOException
import java.net.URI

class ShowReportAction(
    private val reportPath: String? = null
) : AnAction(ACTION_TEXT_OPEN_REPORT) {

    private val logger = Logger.getInstance(this::class.java)

    override fun actionPerformed(actionEvent: AnActionEvent) {
        val reportPath = getReportPath(actionEvent)

        if (reportPath == null) {
            Messages.showMessageDialog(
                actionEvent.project,
                ERROR_NO_MODULE_SELECTED,
                ERROR_TITLE,
                getErrorIcon()
            )
            return
        }

        ApplicationManager.getApplication().invokeLater {
            try {
                Desktop.getDesktop().browse(URI.create("file://$reportPath"))
            } catch (exception: IOException) {
                logger.error("Error while opening report", exception)

                Messages.showMessageDialog(
                    actionEvent.project,
                    ERROR_CANNOT_OPEN_REPORT + reportPath,
                    ERROR_TITLE,
                    getErrorIcon()
                )
            }
        }
    }

    private fun getReportPath(actionEvent: AnActionEvent): String? {
        if (!reportPath.isNullOrBlank()) return reportPath

        val module = MODULE_CONTEXT.getData(actionEvent.dataContext)
            ?: PSI_ELEMENT.getData(actionEvent.dataContext)?.module

        return module?.let { GradleAndroidModel.get(it)?.screenshotReportPath }
    }
}
