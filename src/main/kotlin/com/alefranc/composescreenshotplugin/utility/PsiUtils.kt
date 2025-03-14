package com.alefranc.composescreenshotplugin.utility

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.core.getFqNameWithImplicitPrefix
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

private const val DISPLAY_NAME_MAX_CHARACTERS = 20

val KtNamedFunction.displayName: String
    get() = name.sanitizeName

val KtClass.displayName: String
    get() = name.sanitizeName

val PsiDirectory.displayName: String
    get() = (getFqNameWithImplicitPrefix()?.asString().takeUnless { it.isNullOrBlank() } ?: name).sanitizeName

fun PsiElement.getFqName(): FqName? {
    return when (this) {
        is KtClass -> this.fqName
        is PsiDirectory -> this.getFqNameWithImplicitPrefix()?.takeUnless { it.asString().isBlank() }
        is KtNamedFunction -> this.fqName
        else -> null
    }
}

private val String?.sanitizeName: String
    get() {
        if (this == null) return "Unknown"
        if (length > DISPLAY_NAME_MAX_CHARACTERS) {
            return "${take(DISPLAY_NAME_MAX_CHARACTERS)}..."
        }
        return this
    }
