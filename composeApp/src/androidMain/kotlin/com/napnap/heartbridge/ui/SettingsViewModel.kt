package com.napnap.heartbridge.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class SettingsViewModel: ViewModel() {
    private val _measurementInterval = MutableStateFlow("15")
    val measurementInterval = _measurementInterval

    private val _maxHistory  = MutableStateFlow("30")
    val maxHistory = _maxHistory

    private val _resetEnabled = MutableStateFlow(false)
    val resetEnabled = _resetEnabled

    init {

    }
    fun updateSettings(interval: String, history: String, reset: Boolean) {
        //Temporary
        _measurementInterval.value = interval
        _maxHistory.value = history
        _resetEnabled.value = reset
    }
}