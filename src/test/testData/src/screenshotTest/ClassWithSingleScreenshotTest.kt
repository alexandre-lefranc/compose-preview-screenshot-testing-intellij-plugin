package com.myapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.myapp.ui.theme.MyAppTheme

class ClassWithSingleScreenshotTest {
    @Preview
    @Composable
    fun Test1() {
        MyAppTheme {
            Text("Hello")
        }
    }
}
