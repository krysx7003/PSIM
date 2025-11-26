package com.napnap.heartbridge.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.napnap.heartbridge.Measurement

@Composable
fun HistoryScreen(){

    val rows = listOf<Measurement>(Measurement("02-05-2005","21:37","0"))
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
    ){
        Button(
            onClick = {
                Toast.makeText(
                        context,
                        "Eksportuje",
                        Toast.LENGTH_SHORT
                    ).show()
            },
            modifier = Modifier
                .padding( 16.dp,10.dp)
                .align(Alignment.End),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(12.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black
            )
        ){
            Text(text = "Eksportuj", style = MaterialTheme.typography.bodySmall)
        }
        TableRow("Data","Godzina","Bpm",3)
        LazyColumn(
          modifier = Modifier.fillMaxSize()
        ) {
            items(rows){ item->
                TableRow(item.date,item.hour,item.bpm,1)
            }
        }
    }
}

@Composable
fun TableRow(date: String,hour: String,bpm: String,thickness:Int){
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            date,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(2f),
            color = MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            hour,
            modifier = Modifier
                .width(110.dp)
                .padding(start = 10.dp),
            color = MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End
        )
        Text(
            bpm,
            modifier = Modifier
                .weight(1f) // Takes available space
                .padding(end = 16.dp),
            color = MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End
        )
    }
    HorizontalDivider(
        color = Color.DarkGray,
        thickness = thickness.dp,
        modifier = Modifier.fillMaxWidth()
    )
}