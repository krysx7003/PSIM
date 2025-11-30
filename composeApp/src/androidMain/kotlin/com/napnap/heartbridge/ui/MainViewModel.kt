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
import kotlin.concurrent.thread

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

    val MineUUID = UUID.fromString("4cdabaa2-2cea-c0c1-b38d-a0481ae60a97")

    fun startBpmMeasurement(gatt: BluetoothGatt) {
        val characteristic = gatt.services
            .flatMap { it.characteristics }
            .firstOrNull { it.uuid == MineUUID }

        characteristic?.let {
            // Przykładowy 21-bajtowy pakiet, tylko 17-ty bajt ustawiamy na 1 do startu pomiaru
            for (i in 0 until 21) {
                val command = ByteArray(21) { 0 }
                command[i] = 1 // bajt kontrolny start/stop pomiaru

                it.value = command
                val success = gatt.writeCharacteristic(it)

                Log.i("BLE-Control", "Wysłano polecenie numer $i START pomiaru BPM: $success")
                Thread.sleep(100)
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun measureBPM(device: BluetoothDevice, context: Context) {
        val HEART_RATE_SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        val HEART_RATE_MEASUREMENT_CHAR_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")

        val bluetoothGattCallback = object : BluetoothGattCallback() {

            var bluetoothGatt: BluetoothGatt? = null

            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                bluetoothGatt = gatt
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

                    val characteristic = gatt.services
                        .flatMap { it.characteristics }
                        .firstOrNull { it.uuid == MineUUID }

                    if (characteristic != null) {
                        gatt.setCharacteristicNotification(characteristic, true)

                        val descriptorUUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb") // Client Characteristic Configuration Descriptor
                        val descriptor = characteristic.getDescriptor(descriptorUUID)
                        if (descriptor != null) {
                            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            gatt.writeDescriptor(descriptor)
                            Log.i("BLE-Test", "Notifications enabled for characteristic $MineUUID")
                        } else {
                            Log.w("BLE-Test", "Descriptor not found for characteristic $MineUUID")
                        }
                    } else {
                        Log.w("BLE-Test", "Characteristic $MineUUID not found")
                    }
                } else {
                    Log.w("BLE-Test", "onServicesDiscovered received: $status")
                }
                bluetoothGatt?.let {
                    startBpmMeasurement(it)
                }
            }


            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                val data = characteristic.value
                if (characteristic.uuid == MineUUID) {
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
                        _bpm.value = bpm.toString()
                    }
                }
            }
        }

        device.connectGatt(context, false, bluetoothGattCallback)
    }
}