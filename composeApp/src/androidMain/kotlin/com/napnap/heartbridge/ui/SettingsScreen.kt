package com.napnap.heartbridge.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(viewModel: SettingsViewModel){
    val measurementIntervalState by viewModel.measurementInterval.collectAsState()
    val maxHistoryState by viewModel.maxHistory.collectAsState()
    val resetEnabledState by viewModel.resetEnabled.collectAsState()

    var measurementInterval by remember { mutableStateOf(TextFieldValue(measurementIntervalState)) }
    var maxHistory by remember { mutableStateOf(TextFieldValue(maxHistoryState)) }
    var resetEnabled by remember { mutableStateOf(resetEnabledState) }

    Column {
        Text(
            "Pomiary",
            modifier = Modifier
                .padding(horizontal = 10.dp),
            fontSize = 40.sp,
            color = Color.Red
        )
        Row{
            Text(
                "Czas pomiędzy pomiarami",
                modifier = Modifier
                    .padding(horizontal = 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Red
            )

            BasicTextField(
                value = measurementInterval,
                onValueChange = { newValue ->
                    if (newValue.text.isEmpty() || newValue.text.all { it.isDigit() }) {
                        measurementInterval = newValue
                    }
                },
                modifier = Modifier
                    .width(90.dp)
                    .padding(horizontal = 10.dp)
                    .background(
                        color = Color.LightGray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
                ,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Red,
                    textAlign = TextAlign.Center
                ),
                singleLine = true,
            )
        }

        Row{
            Text(
                "Pomiar na rządanie reseteuje timer",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Red
            )
            Switch(
                checked = resetEnabled,
                onCheckedChange = { resetEnabled = it },
                modifier = Modifier
                    .weight(0.25f)
                    .padding(end = 10.dp)
            )
        }

        HorizontalDivider(
            color = Color.DarkGray,
            thickness = 3.dp,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
        )

        Text(
            "Historia",
            modifier = Modifier
                .padding(horizontal = 10.dp),
            fontSize = 40.sp,
            color = Color.Red
        )

        Row{
            Text(
                "Maksymalny czas przechowywania danych",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Red
            )
            BasicTextField(
                value = maxHistory,
                onValueChange = { newValue ->
                    if (newValue.text.isEmpty() || newValue.text.all { it.isDigit() }) {
                        maxHistory = newValue
                    }
                },
                modifier = Modifier
                    .weight(0.3f)
                    .width(60.dp)
                    .padding(start= 10.dp ,end = 18.dp)
                    .background(
                        color = Color.LightGray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
                ,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Red,
                    textAlign = TextAlign.Center
                ),
                singleLine = true,
            )
        }
        Button(
            onClick = {
                viewModel.updateSettings(
                    measurementInterval.text,
                     maxHistory.text,
                     resetEnabled
                )
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
            Text(text = "Zapisz", style = MaterialTheme.typography.bodySmall)
        }
    }
}
