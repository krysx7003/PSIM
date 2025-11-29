package com.napnap.heartbridge.ui

import androidx.lifecycle.ViewModel
import com.napnap.heartbridge.Measurement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistoryViewModel: ViewModel() {

    private val _rows = MutableStateFlow<List<Measurement>>(emptyList())
    val rows: StateFlow<List<Measurement>> = _rows.asStateFlow()

    init {
        _rows.value = listOf(
            Measurement("02-05-2025", "08:15", "65"),
            Measurement("02-05-2025", "08:30", "68"),
            Measurement("02-05-2025", "08:45", "72"),
            Measurement("02-05-2025", "09:00", "70"),
            Measurement("02-05-2025", "09:15", "67"),

            Measurement("03-05-2025", "14:20", "74"),
            Measurement("03-05-2025", "14:35", "71"),
            Measurement("03-05-2025", "14:50", "69"),
            Measurement("03-05-2025", "15:05", "76"),
            Measurement("03-05-2025", "15:20", "73")
        )
    }
}