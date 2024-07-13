package com.mebubilisim.trtoolspro.WifiAndBLTestfile

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WifiViewModel(application: Application) : AndroidViewModel(application) {
    private val wifiManager: WifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val _wifiEnabled = MutableStateFlow(false)
    var wifiOnOfController = mutableStateOf(false)
    var wifiController = mutableStateOf(0) // 0: Tarama yapılıyor, 1: Ağlar bulundu, 2: Tarama başarısız
    val wifiEnabled: StateFlow<Boolean> = _wifiEnabled
    private val _availableNetworks = MutableStateFlow<List<ScanResult>>(emptyList())

    private val wifiStateReceiver = object : BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                when (intent?.action) {
                    WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                        val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
                        _wifiEnabled.value = wifiState == WifiManager.WIFI_STATE_ENABLED
                        if (_wifiEnabled.value) {
                            wifiOnOfController.value = false
                            wifiController.value = 0
                            scanWifiNetworks()
                        } else {
                            wifiOnOfController.value = true
                        }
                    }
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> {
                        _availableNetworks.value = wifiManager.scanResults
                        if (_availableNetworks.value.isNotEmpty()) {
                            wifiController.value = 1
                        } else {
                            wifiController.value = 2
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("WifiViewModel", "Error in onReceive: ${e.message}", e)
                wifiController.value = 2 // Set to failure in case of any error
            }
        }
    }

    init {
        try {
            val filter = IntentFilter().apply {
                addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
                addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            }
            application.applicationContext.registerReceiver(wifiStateReceiver, filter)
        } catch (e: Exception) {
            Log.e("WifiViewModel", "Error in init: ${e.message}", e)
        }
    }

    fun enableWifi(context: Context) {
        try {
            if (!wifiManager.isWifiEnabled) {
                wifiOnOfController.value = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } else {
                    wifiManager.isWifiEnabled = true
                }
            }
        } catch (e: Exception) {
            Log.e("WifiViewModel", "Error in enableWifi: ${e.message}", e)
        }
    }

    fun scanWifiNetworks() {
        try {
            val context = getApplication<Application>().applicationContext
            if (_wifiEnabled.value) { // Wi-Fi etkin olup olmadığını kontrol edin
                val success = wifiManager.startScan()
                if (!success) {
                    wifiController.value = 2
                }
            } else {
                wifiController.value = 2 // Wi-Fi kapalıysa başarısız olarak işaretle
            }
        } catch (e: Exception) {
            Log.e("WifiViewModel", "Error in scanWifiNetworks: ${e.message}", e)
            wifiController.value = 2
        }
    }

    override fun onCleared() {
        try {
            super.onCleared()
            getApplication<Application>().applicationContext.unregisterReceiver(wifiStateReceiver)
        } catch (e: Exception) {
            Log.e("WifiViewModel", "Error in onCleared: ${e.message}", e)
        }
    }
}
