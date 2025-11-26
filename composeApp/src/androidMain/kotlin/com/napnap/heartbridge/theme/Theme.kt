package com.napnap.heartbridge.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable


@Composable
fun AppTheme(
    content: @Composable () -> Unit
){
        MaterialTheme(
        typography = typography,
        colorScheme = DarkColorScheme,
        content = content
    )
}
