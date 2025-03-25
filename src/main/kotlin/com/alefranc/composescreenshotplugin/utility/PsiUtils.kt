package com.alefranc.composescreenshotplugin.utility

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.permissions.KaAllowAnalysisOnEdt
import org.jetbrains.kotlin.analysis.api.permissions.allowAnalysisOnEdt
import org.jetbrains.kotlin.analysis.api.resolution.singleConstructorCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaDeclarationSymbol
import org.jetbrains.kotlin.idea.base.plugin.KotlinPluginModeProvider
import org.jetbrains.kotlin.idea.core.getFqNameWithImplicitPrefix
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlinx.serialization.compiler.resolve.toClassDescriptor

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

fun KtNamedFunction.hasAnnotation(classId: ClassId): Boolean {
    return if (KotlinPluginModeProvider.isK2Mode()) {
        mapOnDeclarationSymbol { classId in it.annotations } == true || hasAnnotationInParent(classId)
    } else {
        return descriptor?.annotations?.hasAnnotation(classId.asSingleFqName()) == true ||
            descriptor?.annotations?.any {
                it.type.toClassDescriptor?.annotations?.hasAnnotation(classId.asSingleFqName()) == true
            } == true
    }
}

@OptIn(KaAllowAnalysisOnEdt::class)
private fun KtAnnotated.hasAnnotationInParent(classId: ClassId): Boolean {
    return allowAnalysisOnEdt {
        analyze(this) {
            annotationEntries.find { annotationEntry ->
                val annotationConstructorCall =
                    annotationEntry.resolveToCall()?.singleConstructorCallOrNull() ?: return false
                annotationConstructorCall.symbol.containingDeclaration?.annotations?.contains(classId) == true
            } != null
        }
    }
}

@OptIn(KaAllowAnalysisOnEdt::class)
private inline fun <T> KtAnnotated.mapOnDeclarationSymbol(
    block: KaSession.(KaDeclarationSymbol) -> T?
): T? {
    return when {
        this !is KtDeclaration -> null
        // b/367493550: Function type parameters cannot have a KaSymbol created for them.
        // [Example: foo in `fun f(block: (foo: Any) -> Unit))`.]
        // Skip these elements and let the fallback handling take care of them.
        this is KtParameter && isFunctionTypeParameter -> null
        else -> {
            allowAnalysisOnEdt {
                analyze(this) {
                    block(symbol)
                }
            }
        }
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
