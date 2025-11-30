package com.napnap.heartbridge.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.napnap.heartbridge.JsonManager
import com.napnap.heartbridge.Measurement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistoryViewModel: ViewModel() {

    private val _rows = MutableStateFlow<List<Measurement>>(emptyList())
    val rows: StateFlow<List<Measurement>> = _rows.asStateFlow()

    fun loadData(context: Context){
        _rows.value = JsonManager.load(context)
    }
}