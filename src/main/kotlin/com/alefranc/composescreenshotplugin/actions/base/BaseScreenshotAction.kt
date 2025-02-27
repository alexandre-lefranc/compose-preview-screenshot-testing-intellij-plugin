package com.alefranc.composescreenshotplugin.actions.base

import com.alefranc.composescreenshotplugin.utility.androidModel
import com.alefranc.composescreenshotplugin.utility.capitalizedFirstLetter
import com.alefranc.composescreenshotplugin.utility.displayName
import com.alefranc.composescreenshotplugin.utility.getFqName
import com.alefranc.composescreenshotplugin.utility.runGradle
import com.android.tools.idea.gradle.project.model.GradleAndroidModel
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.externalSystem.task.TaskCallback
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

abstract class BaseScreenshotAction(
    targetElement: PsiElement? = null,
) : BaseAction(targetElement) {

    abstract val gradleCommandLine: String
    abstract val gradleCommandLineOption: String

    abstract fun onTaskSuccess(project: Project, androidModel: GradleAndroidModel)

    override fun update(actionEvent: AnActionEvent) {
        val element = getElement(actionEvent) ?: return
        val targetElementName = when (element) {
            is KtClass -> element.displayName
            is KtNamedFunction -> element.displayName
            is PsiDirectory -> element.displayName
            else -> null
        }

        actionEvent.presentation.apply {
            text = targetElementName?.let { "$actionText '$it'" } ?: actionText
            icon = actionIcon
        }
    }

    override fun actionPerformed(actionEvent: AnActionEvent) {
        val element = getElement(actionEvent) ?: return
        val project = actionEvent.project ?: return
        val androidModel = element.androidModel ?: return
        val filterPattern = "${element.getFqName()}*"
        val gradleCommandLine = getGradleCommandLine(androidModel, filterPattern)

        val taskCallback = object : TaskCallback {
            override fun onSuccess() {
                onTaskSuccess(project, androidModel)
            }

            override fun onFailure() {
                // Do nothing
            }
        }

        runGradle(project, gradleCommandLine, taskCallback)
    }

    private fun getGradleCommandLine(androidModule: GradleAndroidModel, filterPattern: String?): String {
        val gradleProjectPath = androidModule.androidProject.projectPath.projectPath
        val variant = androidModule.selectedVariant

        val gradleTaskName =
            "$gradleProjectPath:$gradleCommandLine${variant.name.capitalizedFirstLetter()}ScreenshotTest"
        val gradleTaskOption = filterPattern?.let { "--$gradleCommandLineOption '$it'" }

        return "$gradleTaskName $gradleTaskOption"
    }
}
