package com.pikciu.stopwatchble.ui

import android.bluetooth.BluetoothAdapter
import androidx.lifecycle.ViewModel
import com.pikciu.stopwatchble.Stopwatch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*
import kotlin.concurrent.timer

class TimerViewModel(bluetoothAdapter: BluetoothAdapter): ViewModel() {
    val stopwatch = Stopwatch(bluetoothAdapter) { timestamp ->
        updateState(timestamp)
    }
    private var timer: Timer? = null
    private var state: State = State.Ready
    private val _time = MutableStateFlow(state.time)
    val time: StateFlow<Time> = _time.asStateFlow()

    private fun updateState(timestamp: Int) {
        state = state.next(timestamp)
        _time.update { state.time }

        if (state is State.Started) {
            val startTime = Date().time
            timer = timer(period = 10) {
                val millis = Date().time - startTime
                _time.update { Time(seconds = millis / 1000.0) }
            }
        } else {
            timer?.cancel()
        }
    }
}

data class Time(
    val seconds: Double
) {
    val text: String
        get() = String.format(Locale.US, "%.2f", seconds)
}

sealed class State {
    object Ready : State() {
        override val time = Time(seconds = 0.0)
        override fun next(timestamp: Int) = Started(timestamp)
    }

    class Started(private val startTimestamp: Int): State() {
        override val time = Time(seconds = 0.0)
        override fun next(timestamp: Int) = Stop(startTimestamp, timestamp)
    }

    class Stop(startTimestamp: Int, stopTimestamp: Int): State() {
        override val time = Time(seconds = (stopTimestamp - startTimestamp) / 1000.0)
        override fun next(timestamp: Int) = Ready
    }

    abstract val time: Time
    abstract fun next(timestamp: Int): State
}