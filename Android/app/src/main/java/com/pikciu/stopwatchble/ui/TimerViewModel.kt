package com.pikciu.stopwatchble.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class TimerViewModel: ViewModel() {
    private val _time = MutableStateFlow(Time(seconds = 0.0))
    val time: StateFlow<Time> = _time.asStateFlow()
}

data class Time(
    val seconds: Double
) {
    val text: String
        get() = String.format(Locale.US, "%.2f", seconds)
}