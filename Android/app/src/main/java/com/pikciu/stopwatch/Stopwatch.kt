package com.pikciu.stopwatch

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class Stopwatch(private val bluetoothAdapter: BluetoothAdapter, private val context: Context, private val callback: (Int) -> Unit) {
    companion object {
        const val TAG = "STOPWATCH"
        const val SERVICE_UUID = "a129eaa4-28f6-4a61-a2b7-cb150dfcb92e"
        const val CHARACTERISTIC_UUID = "e5881cf2-7b51-4dd5-a1e4-3514d2fc3235"
    }

    private var isConnecting = false
    private val scanner: BluetoothLeScanner by lazy { bluetoothAdapter.bluetoothLeScanner }

    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            connect(result)
        }
    }

    private var gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            Log.d(TAG, "onServicesDiscovered")
            val service = gatt.getService(UUID.fromString(SERVICE_UUID)) ?: return
            val characteristic = service.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID)) ?: return
            gatt.setCharacteristicNotification(characteristic, true)
        }

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (gatt.connect()) {
                if (gatt.discoverServices()) {
                    Log.d(TAG, "onConnectionStateChange: discover success")
                } else {
                    Log.d(TAG, "onConnectionStateChange: discover fail")
                }
            } else {
                Log.d(TAG, "onConnectionStateChange: connect fail")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            val timestamp = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).int
            callback(timestamp)
            Log.d(TAG, "onCharacteristicChanged $timestamp")
        }
    }

    @SuppressLint("MissingPermission")
    fun start() {
        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(UUID.fromString(SERVICE_UUID)))
            .build()
        val settings = ScanSettings.Builder()
            .setNumOfMatches(1)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()
        scanner.startScan(listOf(filter), settings, scanCallback)
    }

    @SuppressLint("MissingPermission")
    private fun connect(result: ScanResult?) {
        if (isConnecting) {
            return
        }
        val device = result?.device ?: return
        scanner.stopScan(scanCallback)
        device.connectGatt(context, true, gattCallback)
        isConnecting = true
    }
}