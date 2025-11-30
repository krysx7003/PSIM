package com.napnap.heartbridge

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.napnap.heartbridge.ui.components.createNotificationChannel

class MainActivity : ComponentActivity() {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1002
    }

    private lateinit var connectBLE: ConnectBLE
    private fun checkPermissions() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions += Manifest.permission.BLUETOOTH_SCAN
            permissions += Manifest.permission.BLUETOOTH_CONNECT
            permissions += Manifest.permission.ACCESS_FINE_LOCATION
            permissions += Manifest.permission.POST_NOTIFICATIONS
        }

        permissions += Manifest.permission.BLUETOOTH
        permissions += Manifest.permission.BLUETOOTH_ADMIN


        val deniedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (deniedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, deniedPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        checkPermissions()

        connectBLE = ConnectBLE(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        )  {
            connectBLE.onDeviceFound = { device ->
                val name = device.name ?: "Unknown device"
                println("Znaleziono urządzenie: $name (${device.address})")
            }
        }

        createNotificationChannel(applicationContext)

        setContent {
            App()
        }
    }
}

