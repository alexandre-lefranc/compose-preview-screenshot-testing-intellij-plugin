package com.alefranc.composescreenshotplugin.utility

import com.android.tools.idea.gradle.model.IdeVariant
import com.android.tools.idea.gradle.project.model.GradleAndroidModel
import com.intellij.execution.Executor
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultRunExecutor.EXECUTOR_ID
import com.intellij.ide.actions.runAnything.RunAnythingAction.EXECUTOR_KEY
import com.intellij.ide.actions.runAnything.RunAnythingContext
import com.intellij.ide.actions.runAnything.RunAnythingContext.BrowseRecentDirectoryContext
import com.intellij.ide.actions.runAnything.activity.RunAnythingProviderBase.EXECUTING_CONTEXT
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings
import com.intellij.openapi.externalSystem.model.execution.ExternalTaskExecutionInfo
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode.NO_PROGRESS_ASYNC
import com.intellij.openapi.externalSystem.task.TaskCallback
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.util.execution.ParametersListUtil
import org.gradle.cli.CommandLineParser
import org.jetbrains.kotlin.idea.base.projectStructure.externalProjectPath
import org.jetbrains.plugins.gradle.service.execution.cmd.GradleCommandLineOptionsConverter
import org.jetbrains.plugins.gradle.settings.GradleSettings
import org.jetbrains.plugins.gradle.util.GradleConstants.SYSTEM_ID

val PsiElement.androidModel: GradleAndroidModel?
    get() = ModuleUtilCore.findModuleForPsiElement(this)?.let(GradleAndroidModel::get)

val Project.isGradleProject: Boolean
    get() = GradleSettings.getInstance(this).linkedProjectsSettings.isNotEmpty()

val IdeVariant.pathSegments: String
    get() = buildType + "/" + productFlavors.capitalizeExceptFirst().joinToString("")

fun runGradle(project: Project, commandLine: String, taskCallback: TaskCallback) {
    val dataContext = SimpleDataContext.getProjectContext(project)
    val executionContext = dataContext.getData(EXECUTING_CONTEXT) ?: RunAnythingContext.ProjectContext(project)
    val executor = EXECUTOR_KEY.getData(dataContext)
    val workDirectory = executionContext.getProjectPath().orEmpty()

    runGradle(
        project = project,
        executor = executor,
        workDirectory = workDirectory,
        fullCommandLine = commandLine,
        taskCallback = taskCallback,
    )
}

fun runGradle(
    project: Project,
    workDirectory: String,
    fullCommandLine: String,
    executor: Executor?,
    taskCallback: TaskCallback? = null,
) {
    val gradleTaskExecutionInfo = buildGradleTaskInfo(
        projectPath = workDirectory,
        fullCommandLine = fullCommandLine,
        executor = executor,
    )

    ExternalSystemUtil.runTask(
        gradleTaskExecutionInfo.settings,
        gradleTaskExecutionInfo.executorId,
        project,
        SYSTEM_ID,
        taskCallback,
        NO_PROGRESS_ASYNC,
    )

    val configuration = ExternalSystemUtil.createExternalSystemRunnerAndConfigurationSettings(
        gradleTaskExecutionInfo.settings,
        project,
        SYSTEM_ID
    )

    if (configuration != null) {
        val runManager = RunManager.getInstance(project)
        val existingConfiguration = runManager.findConfigurationByTypeAndName(configuration.type, configuration.name)
        if (existingConfiguration == null) {
            runManager.setTemporaryConfiguration(configuration)
        } else {
            runManager.selectedConfiguration = existingConfiguration
        }
    }
}

private fun RunAnythingContext.getProjectPath() = when (this) {
    is RunAnythingContext.ProjectContext ->
        GradleSettings.getInstance(project).linkedProjectsSettings.firstOrNull()
            ?.let { ExternalSystemApiUtil.findProjectNode(project, SYSTEM_ID, it.externalProjectPath) }
            ?.data?.linkedExternalProjectPath
    is RunAnythingContext.ModuleContext -> module.externalProjectPath
    is RunAnythingContext.RecentDirectoryContext -> path
    is BrowseRecentDirectoryContext -> null
}

private fun buildGradleTaskInfo(
    projectPath: String,
    fullCommandLine: String,
    executor: Executor?,
): ExternalTaskExecutionInfo {
    val gradleCmdParser = CommandLineParser()
    val commandLineConverter = GradleCommandLineOptionsConverter().apply { configure(gradleCmdParser) }
    val parsedCommandLine = gradleCmdParser.parse(ParametersListUtil.parse(fullCommandLine, true))
    val optionsMap = commandLineConverter.convert(parsedCommandLine, HashMap())
    val parameters = optionsMap.map { (optionName, optionValues) ->
        if (optionValues.isEmpty()) {
            "--$optionName"
        } else {
            optionValues.map { "--$optionName $it" }.joinToString { " " }
        }
    }.joinToString { " " }

    val settings = ExternalSystemTaskExecutionSettings().apply {
        externalProjectPath = projectPath
        taskNames = parsedCommandLine.extraArguments
        scriptParameters = parameters
        externalSystemIdString = SYSTEM_ID.id
    }

    return ExternalTaskExecutionInfo(settings, executor?.id ?: EXECUTOR_ID)
}
