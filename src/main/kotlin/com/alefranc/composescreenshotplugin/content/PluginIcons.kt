package com.alefranc.composescreenshotplugin.content

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader

object PluginIcons {
    @JvmField
    val ICON_ACTION_CAMERA = IconLoader.getIcon("/icons/camera.svg", javaClass)

    @JvmField
    val ICON_ACTION_DELETE = AllIcons.General.Delete

    @JvmField
    val ICON_ACTION_EXECUTE = AllIcons.Actions.Execute

    @JvmField
    val ICON_ACTION_FIND = AllIcons.Actions.Find

    @JvmField
    val ICON_GUTTER_RUN = AllIcons.RunConfigurations.TestState.Green2
}
