package com.mebubilisim.trtoolspro.WifiAndBLTestfile

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    var GpsStartController = mutableStateOf(false)
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()
    var GPSController = mutableStateOf(0) // 0: Tarama yapılıyor, 1: Ağlar bulundu, 2: Tarama başarısız

    @SuppressLint("MissingPermission")
    fun fetchLocation() {
        viewModelScope.launch {
            try {
                GPSController.value = 0
                val cancellationTokenSource = CancellationTokenSource()
                val location = fusedLocationClient.getCurrentLocation(
                    com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).await()

                if (location != null) {
                    _currentLocation.value = location
                    GPSController.value = 1
                    Log.d("fetchLocation", "fetchLocation: ${_currentLocation.value}")
                } else {
                    GPSController.value = 2

                    Log.e("fetchLocation", "Location fetch failed")
                }
            } catch (e: Exception) {
                GPSController.value = 2
                e.printStackTrace()
            }
        }
    }
}
