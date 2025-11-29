package com.napnap.heartbridge.ui

import androidx.lifecycle.ViewModel
import com.napnap.heartbridge.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel: ViewModel() {
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _bpm = MutableStateFlow("---")
    val bpm: StateFlow<String> = _bpm.asStateFlow()

    private val _devices = MutableStateFlow<List<String>>(emptyList())
    val devices: StateFlow<List<String>> = _devices.asStateFlow()

    private val _device = MutableStateFlow(Device("Brak urządzenia","--%"))
    val device = _device

    init {
        _devices.value = listOf(
            "Samsung Watch 6",
            "Apple Watch Series 9",
            "Fitbit Sense 2"
        )
    }
    fun showDialog() {
        _showDialog.value = true
    }

    fun hideDialog() {
        _showDialog.value = false
    }}