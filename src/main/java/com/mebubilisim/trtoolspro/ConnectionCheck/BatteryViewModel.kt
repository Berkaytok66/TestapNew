package com.mebubilisim.trtoolspro.ConnectionCheck

import SocketServer
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BatteryViewModel(application: Application) : AndroidViewModel(application) {
    var kabloluJarjController = mutableStateOf(0)
    var WirlessJarjController = mutableStateOf(0)
    var BLController = mutableStateOf(0)
    private val _wirlesChanged = MutableStateFlow(false)
    val wirlesChanged: StateFlow<Boolean> = _wirlesChanged
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    private var bluetoothStatusReceiver: BroadcastReceiver
    private val _bluetoothEnabled = MutableStateFlow(false)
    val bluetoothEnabled: StateFlow<Boolean> = _bluetoothEnabled.asStateFlow()
    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices.asStateFlow()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val batteryStatusReceiver = object : BroadcastReceiver() {
        @SuppressLint("SuspiciousIndentation")
        override fun onReceive(context: Context?, intent: Intent?) {
            context?.let {
                val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                val chargePlug = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
                val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
                val wirelessCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS


                    if (isCharging && usbCharge) {
                        kabloluJarjController.value = 1

                        queueMessage {
                            SocketServer.sendMessageToAllClients(
                                statusReason = "Wired charging control",
                                functionName = "WiredChargingViewModel",
                                progress = 100
                            )
                        }

                        return@let
                    } else if (!isCharging && usbCharge) {
                        kabloluJarjController.value = 0
                        queueMessage {
                            SocketServer.sendMessageToAllClients(
                                statusReason = "Wired jar control",
                                functionName = "WiredChargingViewModel",
                                progress = 0
                            )
                        }
                        return@let
                    }


                if (wirelessCharge) {
                    WirlessJarjController.value = 1
                } else if (!isCharging && wirelessCharge) {
                    WirlessJarjController.value = 0
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        context.unregisterReceiver(batteryStatusReceiver)
        context.unregisterReceiver(bluetoothStatusReceiver)
    }

    fun setWirelessChargingEnabled(enabled: Boolean) {
        _wirlesChanged.value = enabled
    }

    init {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(batteryStatusReceiver, filter)
        bluetoothStatusReceiver = createBluetoothReceiver()
        initBluetoothReceiver()
        initBluetoothStatus()
    }

    private fun initBluetoothStatus() {
        val initialState = bluetoothAdapter?.state
        _bluetoothEnabled.value = initialState == BluetoothAdapter.STATE_ON
        BLController.value = if (initialState == BluetoothAdapter.STATE_ON) 1 else 2

        queueMessage {
            SocketServer.sendMessageToAllClients(
                message = if (initialState == BluetoothAdapter.STATE_ON) "Bluetooth enabled" else "Bluetooth disabled",
                success = initialState == BluetoothAdapter.STATE_ON,
                statusReason = "Bluetooth status",
                functionName = "initBluetoothStatus",
                progress = if (initialState == BluetoothAdapter.STATE_ON) 100 else 0
            )
        }
    }

    private fun createBluetoothReceiver() = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    when (state) {
                        BluetoothAdapter.STATE_ON -> {
                            _bluetoothEnabled.value = true
                            BLController.value = 1
                            queueMessage {
                                SocketServer.sendMessageToAllClients(
                                    message = "Bluetooth enabled",
                                    success = true,
                                    statusReason = "Bluetooth status",
                                    functionName = "initBluetoothStatus",
                                    progress = 100
                                )
                            }
                        }
                        BluetoothAdapter.STATE_TURNING_ON -> {
                            BLController.value = 0
                        }
                        BluetoothAdapter.STATE_OFF -> {
                            _bluetoothEnabled.value = false
                            BLController.value = 2
                            queueMessage {
                                SocketServer.sendMessageToAllClients(
                                    message = "Bluetooth disabled",
                                    success = false,
                                    statusReason = "Bluetooth status",
                                    functionName = "initBluetoothStatus",
                                    progress = 0
                                )
                            }
                        }
                    }
                    discoverDevices()
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    _discoveredDevices.value = _discoveredDevices.value + device
                }
            }
        }
    }

    private fun initBluetoothReceiver() {
        val bluetoothFilter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_FOUND)
        }
        context.registerReceiver(bluetoothStatusReceiver, bluetoothFilter)
    }

    @SuppressLint("MissingPermission")
    fun discoverDevices() {
        if (bluetoothAdapter?.isEnabled == true) {
            bluetoothAdapter.startDiscovery()
        } else {
            enableBluetooth()
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableBluetooth() {
        if (bluetoothAdapter?.isEnabled != true) {
            bluetoothAdapter?.enable()
        }
    }

    private fun queueMessage(message: () -> Unit) {
        coroutineScope.launch {
            delay(500) // Her mesaj arasında 500ms gecikme
            message()
        }
    }
}
