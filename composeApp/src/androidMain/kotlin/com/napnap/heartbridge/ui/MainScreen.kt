package com.napnap.heartbridge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery0Bar
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(){
    Column(
        modifier = Modifier.fillMaxWidth(),
    ){
        val text = "---"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            if (text.length == 3){
                Spacer(modifier = Modifier.weight(4f))
            }else if(text.length == 2){
                Spacer(modifier = Modifier.weight(2.5f))

            }

            Text(
                text,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Red
            )
            Text(
                "BPM",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Red
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Button(
            onClick = { /* */},
            modifier = Modifier
                .padding( 16.dp, 5.dp )
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            enabled = true,
            contentPadding = PaddingValues(12.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Icon(Icons.Default.Watch,"",modifier = Modifier.padding(start = 10.dp))
                Text(text = "Urządzenie")
                Icon(Icons.Default.Battery0Bar,"",modifier = Modifier.padding(end = 10.dp))
            }
        }

        Button(
            onClick = { /* */},
            modifier = Modifier
                .padding( 16.dp,5.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            enabled = true,
            contentPadding = PaddingValues(12.dp)
        ){
            Text(text = "Wyszukaj urządzenia")
        }

    }

}
