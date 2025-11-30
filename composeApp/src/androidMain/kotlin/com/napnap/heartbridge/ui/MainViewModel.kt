package com.napnap.heartbridge.ui

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.napnap.heartbridge.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.napnap.heartbridge.ConnectBLE

class MainViewModel: ViewModel() {
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _bpm = MutableStateFlow("---")
    val bpm: StateFlow<String> = _bpm.asStateFlow()

    private val _devices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val devices: StateFlow<List<BluetoothDevice>> = _devices.asStateFlow()

    private val _device = MutableStateFlow<BluetoothDevice?>(null)
    val device :MutableStateFlow<BluetoothDevice?> = _device

    var connectBLE:ConnectBLE? = null

    fun initConnection(context: Context){
            connectBLE = ConnectBLE(context)
        }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun showDialog(context: Context) {
        _showDialog.value = true
        connectBLE?.onDeviceFound = { device ->
            val currentList = _devices.value ?: emptyList()
            if (!currentList.any { it.address == device.address }) {
                _devices.value = currentList + device
            }
        }
        connectBLE?.startScan()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun hideDialog() {
        _showDialog.value = false
        connectBLE?.stopScan()
        if (connectBLE?.getConnectedDevice() != null)
            _device.value = connectBLE?.getConnectedDevice()
    }

    fun getBatteryLevel(device: BluetoothDevice, context: Context): String {
        val filter = IntentFilter("android.bluetooth.device.action.BATTERY_LEVEL_CHANGED")
        var batlvl = "100%"
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val batteryLevel = intent.getIntExtra("android.bluetooth.device.extra.BATTERY_LEVEL", -1)
                if (batteryLevel != -1) {
                    batlvl = batteryLevel.toString()
                }
            }
        }
        context.registerReceiver(receiver, filter)
        return batlvl
    }
}