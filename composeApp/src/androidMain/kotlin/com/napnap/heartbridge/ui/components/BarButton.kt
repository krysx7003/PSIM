package com.napnap.heartbridge.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BarButton(click: () -> Unit, icon: ImageVector,
              description: String, modifier: Modifier = Modifier){
    IconButton(
        onClick = { click() },
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
