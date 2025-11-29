package com.napnap.heartbridge

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.napnap.heartbridge.ui.components.createNotificationChannel

class MainActivity : ComponentActivity() {
    companion object {
        private const val NOTIFICATION_PERMISSION_CODE = 1001
        private const val PERMISSION_REQUEST_CODE = 1002
    }

    private lateinit var connectBLE: ConnectBLE

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startBleScan() {
        connectBLE.startScan()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopBleScan() {
        connectBLE.stopScan()
    }

    fun getDevices(): List<BluetoothDevice> {
        return connectBLE.getFoundDevices()
    }

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

        val name = "Unknown device"
        connectBLE.onDeviceFound = { device ->
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) else {
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

