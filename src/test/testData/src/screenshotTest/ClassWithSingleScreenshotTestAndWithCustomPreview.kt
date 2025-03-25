package com.myapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.myapp.ui.theme.MyAppTheme

class ClassWithSingleScreenshotTest {
    @CustomPreviews
    @Composable
    fun Test1() {
        MyAppTheme {
            Text("Hello")
        }
    }
}

@Preview(
    name = "foo",
)
@Preview(
    name = "bar",
)
annotation class CustomPreviews
