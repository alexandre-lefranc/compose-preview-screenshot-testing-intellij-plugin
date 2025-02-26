package com.myapp

import androidx.compose.runtime.Composable
import com.myapp.ui.theme.MyAppTheme

class ClassWithoutScreenshotTest {
    @Composable
    fun BasicComposable() {
        MyAppTheme {
            Text("Hello")
        }
    }

    fun basicFunction() {
    }
}
