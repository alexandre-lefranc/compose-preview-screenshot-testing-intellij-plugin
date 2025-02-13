package com.alefranc.composescreenshotplugin.navHandlers

import com.alefranc.composescreenshotplugin.actions.DeleteReferencesAction
import com.alefranc.composescreenshotplugin.actions.RecordAction
import com.alefranc.composescreenshotplugin.actions.ShowReferencesAction
import com.alefranc.composescreenshotplugin.actions.VerifyAction
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.JBPopupFactory.ActionSelectionAid.SPEEDSEARCH
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore
import com.intellij.ui.awt.RelativePoint
import java.awt.event.MouseEvent

class ScreenshotClassNavHandler(
    private val targetElement: PsiElement,
) : GutterIconNavigationHandler<PsiElement> {

    override fun navigate(event: MouseEvent?, nameIdentifier: PsiElement) {
        if (event == null) return

        val project = nameIdentifier.project
        val virtualFile = PsiUtilCore.getVirtualFile(nameIdentifier.parent) ?: return
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)

        if (virtualFile == psiFile?.virtualFile) {
            editor.caretModel.moveToOffset(nameIdentifier.textOffset)

            val dataContext = DataManager.getInstance().getDataContext(event.component)
            createActionGroupPopup(dataContext).show(RelativePoint(event))
        }
    }

    private fun createActionGroupPopup(dataContext: DataContext): JBPopup {
        val group = DefaultActionGroup(
            VerifyAction(targetElement),
            RecordAction(targetElement),
            ShowReferencesAction(targetElement),
            DeleteReferencesAction(targetElement),
        )

        return JBPopupFactory.getInstance().createActionGroupPopup(
            null,
            group,
            dataContext,
            SPEEDSEARCH,
            true,
        )
    }
}
