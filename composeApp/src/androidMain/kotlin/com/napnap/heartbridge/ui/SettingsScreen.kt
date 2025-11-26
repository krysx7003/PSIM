package com.napnap.heartbridge.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(){
    Column {
        Text(
            "Pomiary",
            modifier = Modifier
                .padding(horizontal = 10.dp),
            fontSize = 40.sp,
            color = Color.Red
        )


        HorizontalDivider(
            color = Color.DarkGray,
            thickness = 3.dp,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        )

        Text(
            "Historia",
            modifier = Modifier
                .padding(horizontal = 10.dp),
            fontSize = 40.sp,
            color = Color.Red
        )


    }

}
