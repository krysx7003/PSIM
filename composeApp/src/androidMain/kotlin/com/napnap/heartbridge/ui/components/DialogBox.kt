@file:OptIn(ExperimentalMaterial3Api::class)

package com.napnap.heartbridge.ui.components

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.napnap.heartbridge.ConnectBLE
import com.napnap.heartbridge.ui.MainViewModel

@Composable

fun DialogBox(devices: List<BluetoothDevice>, onDismissRequest: () -> Unit, viewModel: MainViewModel ){
    val context = LocalContext.current

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ){
        Box(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(4.dp)
                )
        ){
            Column{
                Text(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    text = "Znaleziono urządzenia" ,
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider(
                    color = Color.DarkGray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn {
                    items(devices){ item ->
                        Button(
                            onClick = {viewModel.connectBLE?.connectToDevice(device = item)
                                      onDismissRequest()},
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(12.dp),
                            shape = RoundedCornerShape(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.Red,
                                containerColor = Color.White,
                            )
                        ){
                            val name = item.name ?: "NONAME"
                            Text(
                                text = name,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = MaterialTheme.colorScheme.onSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }

                HorizontalDivider(
                    color = Color.DarkGray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { onDismissRequest() },
                    modifier = Modifier
                        .align(Alignment.End),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                    )
                ){
                    Text(
                        text = "Anuluj" ,
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

    }
}

