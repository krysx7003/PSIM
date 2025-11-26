package com.napnap.heartbridge.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.napnap.heartbridge.ui.components.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val _measurementInterval = MutableStateFlow("15")
    val measurementInterval = _measurementInterval

    private val _maxHistory  = MutableStateFlow("30")
    val maxHistory = _maxHistory

    private val _resetEnabled = MutableStateFlow(false)
    val resetEnabled = _resetEnabled


    init {
        _measurementInterval.value = SettingsStore.readS(application,"interval","15")
        _maxHistory.value = SettingsStore.readS(application,"history","30")
        _resetEnabled.value = SettingsStore.readB(application,"reset",false)
    }
    fun updateSettings(interval: String, history: String, reset: Boolean) {

        _measurementInterval.value = interval
        _maxHistory.value = history
        _resetEnabled.value = reset

        val application: Application = getApplication()

        SettingsStore.saveS(application,"interval",interval)
        SettingsStore.saveS(application,"history",history)
        SettingsStore.saveB(application,"reset",reset)
    }
}