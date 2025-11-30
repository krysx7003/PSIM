package com.napnap.heartbridge.ui

import android.Manifest
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
import com.napnap.heartbridge.ConnectBLE
import com.napnap.heartbridge.JsonManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class MainViewModel: ViewModel() {
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _bpm = MutableStateFlow("---")
    val bpm: StateFlow<String> = _bpm.asStateFlow()
    private var measurements: List<Int> = emptyList()

    private val _devices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val devices: StateFlow<List<BluetoothDevice>> = _devices.asStateFlow()

    private val _device = MutableStateFlow<BluetoothDevice?>(null)
    val device :MutableStateFlow<BluetoothDevice?> = _device

    var connectBLE:ConnectBLE? = null

    fun initConnection(context: Context){
        connectBLE = ConnectBLE(context)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun showDialog() {
        _showDialog.value = true
        connectBLE?.onDeviceFound = { device ->
            val currentList = _devices.value
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
        if (connectBLE?.getConnectedDevice() != null){
            _device.value = connectBLE?.getConnectedDevice()
            measureBPM(_device.value!!, context)
        }
    }

    fun getBatteryLevel( context: Context): String {
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

    val bpmCharUUID:UUID = UUID.fromString("4cdabaa2-2cea-c0c1-b38d-a0481ae60a97")

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun startBpmMeasurement(gatt: BluetoothGatt) {
        val characteristic = gatt.services
            .flatMap { it.characteristics }
            .firstOrNull { it.uuid == bpmCharUUID }

        characteristic?.let  {
            // Przykładowy 21-bajtowy pakiet, tylko 17-ty bajt ustawiamy na 1 do startu pomiaru
            for (i in 0 until 21) {
                val command = ByteArray(21)
                command[i] = 1 // bajt kontrolny start/stop pomiaru

                it.value = command
                val success = gatt.writeCharacteristic(it)

                Log.i("BLE-Control", "Wysłano polecenie numer $i START pomiaru BPM: $success")
                Thread.sleep(100)
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun saveMeasurement(context: Context, receivedBpm:Int){
        measurements += receivedBpm
        if(measurements.size > 10){
            JsonManager.appendMany(context,measurements,_device.value!!.name)
            measurements = emptyList()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun measureBPM(device: BluetoothDevice, context: Context) {

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
                        .firstOrNull { it.uuid == bpmCharUUID }

                    if (characteristic != null) {
                        gatt.setCharacteristicNotification(characteristic, true)

                        val descriptorUUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb") // Client Characteristic Configuration Descriptor
                        val descriptor = characteristic.getDescriptor(descriptorUUID)
                        if (descriptor != null) {
                            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            gatt.writeDescriptor(descriptor)
                            Log.i("BLE-Test", "Notifications enabled for characteristic $bpmCharUUID")
                        } else {
                            Log.w("BLE-Test", "Descriptor not found for characteristic $bpmCharUUID")
                        }
                    } else {
                        Log.w("BLE-Test", "Characteristic $bpmCharUUID not found")
                    }
                } else {
                    Log.w("BLE-Test", "onServicesDiscovered received: $status")
                }
                bluetoothGatt?.let {
                    startBpmMeasurement(it)
                }
            }


            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                val data = characteristic.value
                if (characteristic.uuid == bpmCharUUID) {
                    Log.i(
                        "BLE-Test",
                        "Data from characteristic ${characteristic.uuid}: ${
                            data?.joinToString(", ") {
                                it.toUByte().toString()
                            }
                        }"
                    )
                    if (data != null && data.isNotEmpty()) {
                        val receivedBpm = data[13].toUByte().toInt()
                        Log.i("BLE-Test", "BPM from ${characteristic.uuid}: $receivedBpm")
                        _bpm.value = receivedBpm.toString()
                        saveMeasurement(context,receivedBpm)
                    }
                }
            }
        }

        device.connectGatt(context, false, bluetoothGattCallback)
    }
}