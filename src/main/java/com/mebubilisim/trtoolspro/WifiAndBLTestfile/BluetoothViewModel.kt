package com.mebubilisim.trtoolspro.ConnectionCheck

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BluetoothViewModel(application: Application) : AndroidViewModel(application) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    var bluetoothController = mutableStateOf(0) // 0: Tarama yapılıyor, 1: Cihazlar bulundu, 2: Tarama başarısız
    var bluetoothOnOfController = mutableStateOf(false)
    private val _bluetoothEnabled = MutableStateFlow(false)
    val bluetoothEnabled: StateFlow<Boolean> = _bluetoothEnabled
    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices

    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    _bluetoothEnabled.value = (state == BluetoothAdapter.STATE_ON)
                    if (_bluetoothEnabled.value) {
                        bluetoothOnOfController.value = false
                        bluetoothController.value = 0
                        startDiscovery()
                    } else {
                        bluetoothOnOfController.value = true
                    }
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        _discoveredDevices.value = _discoveredDevices.value + it
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    if (_discoveredDevices.value.isNotEmpty()) {
                        bluetoothController.value = 1
                    } else {
                        bluetoothController.value = 2
                    }
                }
            }
        }
    }

    init {
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        application.applicationContext.registerReceiver(bluetoothStateReceiver, filter)
    }

    @SuppressLint("MissingPermission")
    fun enableBluetooth(context: Context) {
        if (bluetoothAdapter?.isEnabled == false) {
            bluetoothOnOfController.value = true
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    @SuppressLint("MissingPermission")
    fun startDiscovery() {
        bluetoothAdapter?.cancelDiscovery() // Eski taramayı iptal edin
        val success = bluetoothAdapter?.startDiscovery() == true
        if (!success) {
            bluetoothController.value = 2
        }
    }

    override fun onCleared() {
        super.onCleared()
        getApplication<Application>().applicationContext.unregisterReceiver(bluetoothStateReceiver)
    }
}
