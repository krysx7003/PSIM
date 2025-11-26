package com.napnap.heartbridge.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery0Bar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(){
    Column(
        modifier = Modifier.fillMaxWidth(),
    ){
        val text = "---"
        val context = LocalContext.current
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable{
                    Toast.makeText(
                        context,
                        "No device connected",
                        Toast.LENGTH_SHORT
                    ).show()
                },
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
                fontSize = 50.sp,
                color = Color.Red
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Box(
            modifier = Modifier
                .padding( 16.dp, 5.dp )
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
        ){
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth() ,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text( "Brak urządzenia", style = MaterialTheme.typography.bodySmall)
                Row{
                    Text( "--%", style = MaterialTheme.typography.bodySmall)
                    Icon(
                        Icons.Default.Battery0Bar,
                        "",
                        modifier = Modifier.padding(end = 10.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Button(
            onClick = { /* */},
            modifier = Modifier
                .padding( 16.dp,5.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(12.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black
            )
        ){
            Text(text = "Wyszukaj urządzenia", style = MaterialTheme.typography.bodySmall)
        }

    }

}
