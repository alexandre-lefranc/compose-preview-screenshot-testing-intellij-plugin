package com.myapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.myapp.ui.theme.MyAppTheme

@Preview(
    name = "foo",
)
@Preview(
    name = "bar",
)
annotation class CustomPreviews

class ManyScreenshotTestsClass {
    @Preview
    @Composable
    fun Test1() {
        MyAppTheme {
            Text("Hello")
        }
    }

    @Composable
    @Preview
    fun Test2() {
        MyAppTheme {
            Text("Hello")
        }
    }

    @Composable
    @CustomPreviews
    fun Test2() {
        MyAppTheme {
            Text("Hello")
        }
    }


    fun basicFunction() {
    }

    @Composable
    fun BasicComposable() {
        Text("bar")
    }
}
