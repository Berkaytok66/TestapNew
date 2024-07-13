package com.mebubilisim.trtoolspro.EquipmentPage

import android.app.Application
import android.view.KeyEvent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KeyEventViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)

    private val _volumeUpPressed = MutableStateFlow(preferencesManager.getBoolean("volumeUpPressed", false))
    val volumeUpPressed: StateFlow<Boolean> = _volumeUpPressed

    private val _volumeDownPressed = MutableStateFlow(preferencesManager.getBoolean("volumeDownPressed", false))
    val volumeDownPressed: StateFlow<Boolean> = _volumeDownPressed

    private val _powerButtonPressed = MutableStateFlow(preferencesManager.getBoolean("powerButtonPressed", false))
    val powerButtonPressed: StateFlow<Boolean> = _powerButtonPressed

    fun onKeyEvent(keyCode: Int) {
        viewModelScope.launch {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    _volumeUpPressed.value = true
                    preferencesManager.saveBoolean("volumeUpPressed", true)
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    _volumeDownPressed.value = true
                    preferencesManager.saveBoolean("volumeDownPressed", true)
                }
            }
        }
    }

    fun onScreenOff() {
        viewModelScope.launch {
            _powerButtonPressed.value = true
            preferencesManager.saveBoolean("powerButtonPressed", true)
        }
    }

    fun resetKeyStates() {
        viewModelScope.launch {
            _volumeUpPressed.value = false
            _volumeDownPressed.value = false
            _powerButtonPressed.value = false
            preferencesManager.saveBoolean("volumeUpPressed", false)
            preferencesManager.saveBoolean("volumeDownPressed", false)
            preferencesManager.saveBoolean("powerButtonPressed", false)
        }
    }
}
