package com.pikciu.stopwatchble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.pikciu.stopwatchble.ui.TimerView
import com.pikciu.stopwatchble.ui.TimerViewModel
import com.pikciu.stopwatchble.ui.theme.StopwatchBLETheme

class MainActivity : ComponentActivity() {

    private val bluetoothAdapter: BluetoothAdapter by lazy { getSystemService(BluetoothManager::class.java).adapter }
    private val timerViewModel: TimerViewModel by lazy { TimerViewModel(bluetoothAdapter) }
    private val enableBluetoothRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        timerViewModel.stopwatch.start()
    }
    private val bluetoothPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        enableBluetooth()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StopwatchBLETheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    TimerView(timerViewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enableBluetooth()
    }

    private fun enableBluetooth() {
        if (bluetoothAdapter.isEnabled) {
            timerViewModel.stopwatch.start()
            return
        }
        if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothRequest.launch(enableIntent)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bluetoothPermissionRequest.launch(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }
}