package com.alefranc.composescreenshotplugin.actions

import com.alefranc.composescreenshotplugin.actions.base.BaseScreenshotAction
import com.alefranc.composescreenshotplugin.utility.PluginIcons
import com.android.tools.idea.gradle.project.model.GradleAndroidModel
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import javax.swing.Icon

class RecordAction(
    anchorElement: PsiElement? = null,
) : BaseScreenshotAction(anchorElement) {

    override val actionIcon: Icon = PluginIcons.Camera
    override val actionText: String = "Record"

    override val gradleCommandLine: String = "update"
    override val gradleCommandLineOption: String = "updateFilter"

    override fun onTaskSuccess(project: Project, androidModel: GradleAndroidModel) {
        // Do nothing
    }
}
