package com.alefranc.composescreenshotplugin.utility

fun String.capitalizedFirstLetter(): String {
    return replaceFirstChar { it.uppercase() }
}

fun List<String>.capitalizeExceptFirst(): List<String> {
    return this.mapIndexed { index, word ->
        if (index == 0) {
            word.lowercase()
        } else {
            word.capitalizedFirstLetter()
        }
    }
}
