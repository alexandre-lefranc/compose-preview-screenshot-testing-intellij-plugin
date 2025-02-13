package com.alefranc.composescreenshotplugin.actions.base

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.ActionUpdateThread.BGT
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys.PSI_ELEMENT
import com.intellij.psi.PsiElement
import javax.swing.Icon

abstract class BaseAction(
    private val targetElement: PsiElement? = null,
) : AnAction() {
    abstract val actionText: String
    abstract val actionIcon: Icon

    override fun getActionUpdateThread(): ActionUpdateThread {
        return BGT
    }

    fun getElement(actionEvent: AnActionEvent): PsiElement? {
        return targetElement ?: actionEvent.getData(PSI_ELEMENT)
    }
}
