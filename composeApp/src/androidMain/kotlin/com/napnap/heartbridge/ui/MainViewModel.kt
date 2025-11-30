package com.napnap.heartbridge.ui

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.napnap.heartbridge.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.napnap.heartbridge.ConnectBLE
import java.util.UUID

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

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    fun hideDialog(context: Context) {
        _showDialog.value = false
        connectBLE?.stopScan()
        if (connectBLE?.getConnectedDevice() != null)
            _device.value = connectBLE?.getConnectedDevice()
            measureBPM(_device.value!!, context)
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

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun measureBPM(device: BluetoothDevice, context: Context) {
        val HEART_RATE_SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        val HEART_RATE_MEASUREMENT_CHAR_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")

        val bluetoothGattCallback = object : BluetoothGattCallback() {
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d("BPM", "Disconnected from device")
                }
            }

            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val targetCharacteristicUUID = UUID.fromString("4cdabaa2-2cea-c0c1-b38d-a0481ae60a97")

                    val characteristic = gatt.services
                        .flatMap { it.characteristics }
                        .firstOrNull { it.uuid == targetCharacteristicUUID }

                    if (characteristic != null) {
                        gatt.setCharacteristicNotification(characteristic, true)

                        val descriptorUUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb") // Client Characteristic Configuration Descriptor
                        val descriptor = characteristic.getDescriptor(descriptorUUID)
                        if (descriptor != null) {
                            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            gatt.writeDescriptor(descriptor)
                            Log.i("BLE-Test", "Notifications enabled for characteristic $targetCharacteristicUUID")
                        } else {
                            Log.w("BLE-Test", "Descriptor not found for characteristic $targetCharacteristicUUID")
                        }
                    } else {
                        Log.w("BLE-Test", "Characteristic $targetCharacteristicUUID not found")
                    }
                } else {
                    Log.w("BLE-Test", "onServicesDiscovered received: $status")
                }
            }


            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                val data = characteristic.value
                if (characteristic.uuid.toString() == "4cdabaa2-2cea-c0c1-b38d-a0481ae60a97") {
                    Log.i(
                        "BLE-Test",
                        "Data from characteristic ${characteristic.uuid}: ${
                            data?.joinToString(", ") {
                                it.toUByte().toString()
                            }
                        }"
                    )
                    if (data != null && data.isNotEmpty()) {
                        val bpm = data[13].toUByte().toInt()
                        Log.i("BLE-Test", "BPM from ${characteristic.uuid}: $bpm")
                    }
                }
            }
        }

        device.connectGatt(context, false, bluetoothGattCallback)
    }
}