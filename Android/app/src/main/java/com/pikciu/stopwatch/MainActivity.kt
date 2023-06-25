package com.pikciu.stopwatch

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.pikciu.stopwatch.ui.TimerView
import com.pikciu.stopwatch.ui.TimerViewModel
import com.pikciu.stopwatch.ui.theme.StopwatchBLETheme

class MainActivity : ComponentActivity() {

    private val bluetoothAdapter: BluetoothAdapter by lazy { getSystemService(BluetoothManager::class.java).adapter }
    private val timerViewModel: TimerViewModel by lazy { TimerViewModel(bluetoothAdapter, this) }
    private val enableBluetoothRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            startStopwatch()
        }
    }
    private val bluetoothPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            checkBluetoothPermissions()
        }
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
        checkBluetoothPermissions()
    }

    private fun checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                bluetoothPermissionRequest.launch(Manifest.permission.BLUETOOTH_SCAN)
            } else if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                bluetoothPermissionRequest.launch(Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                enableBluetooth()
            }
        } else {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                bluetoothPermissionRequest.launch(Manifest.permission.BLUETOOTH)
            } else if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                bluetoothPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                enableBluetooth()
            }
        }
    }

    private fun enableBluetooth() {
        if (bluetoothAdapter.isEnabled) {
            startStopwatch()
        } else {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothRequest.launch(enableIntent)
        }
    }

    private fun startStopwatch() {
        if (!checkLocation()) {
            timerViewModel.showLocationAlert()
        } else {
            timerViewModel.stopwatch.start()
        }
    }

    private fun checkLocation(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return true
        }
        val locationManager = getSystemService(LocationManager::class.java)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }
}