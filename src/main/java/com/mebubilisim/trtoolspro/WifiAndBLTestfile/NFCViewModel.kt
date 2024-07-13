package com.mebubilisim.trtoolspro.WifiAndBLTestfile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NFCViewModel(application: Application) : AndroidViewModel(application), NfcAdapter.ReaderCallback {
    private val nfcAdapter: NfcAdapter? = (application.getSystemService(Context.NFC_SERVICE) as NfcManager).defaultAdapter
    val isNfcSupported = mutableStateOf(nfcAdapter != null)
    var NFCController = mutableStateOf(0)
    val isNfcEnabled = mutableStateOf(nfcAdapter?.isEnabled ?: false)
    val showEnableNfcDialog = mutableStateOf(false)

    private val _nfcTagDetected = MutableStateFlow<Tag?>(null)

    @SuppressLint("StaticFieldLeak")
    private var activity: Activity? = null

    init {
        if (isNfcSupported.value) {
            checkNfcStatusAndStartListening()
        } else {
            // NFC desteklenmiyor, NFC ile ilgili işlemleri bildirip atlayın
            NFCController.value = 2 // NFC is not supported
            Log.e("NFCViewModel", "NFC is not supported by this device.")
        }
    }

    fun setActivity(activity: Activity) {
        this.activity = activity
    }

    private fun checkNfcStatusAndStartListening() {
        isNfcEnabled.value = nfcAdapter?.isEnabled ?: false
        if (isNfcEnabled.value) {
            NFCController.value = 0
            startNfcListening()
        } else {
            NFCController.value = 2 // NFC is supported but not enabled
            showEnableNfcDialog.value = true
            Log.e("NFCViewModel", "NFC is supported but not enabled.")
        }
    }

    private fun startNfcListening() {
        if (isNfcSupported.value) {
            viewModelScope.launch(Dispatchers.Main) {
                try {
                    activity?.let {
                        nfcAdapter?.enableReaderMode(
                            it,
                            this@NFCViewModel,
                            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B or
                                    NfcAdapter.FLAG_READER_NFC_F or NfcAdapter.FLAG_READER_NFC_V,
                            null
                        )
                        NFCController.value = 0
                        Log.d("NFCViewModel", "NFC Reader Mode enabled")
                        return@launch
                    } ?: run {
                        NFCController.value = 2
                        Log.e("NFCViewModel", "Activity reference is null")
                    }
                } catch (e: Exception) {
                    NFCController.value = 2
                    Log.e("NFCViewModel", "Error enabling NFC Reader Mode", e)
                }
            }
        }
    }

    fun stopNfcListening() {
        if (isNfcSupported.value) {
            if (nfcAdapter != null) {
                activity?.let {
                    nfcAdapter.disableReaderMode(it)
                    Log.d("NFCViewModel", "NFC Reader Mode disabled")
                }
            }
        }
    }

    override fun onTagDiscovered(tag: Tag?) {
        if (isNfcSupported.value) {
            viewModelScope.launch(Dispatchers.Main) {
                _nfcTagDetected.value = tag
                if (tag != null) {
                    NFCController.value = 1
                    Log.d("NFCViewModel", "NFC Tag discovered: ${tag.id?.contentToString()}")
                } else {
                    NFCController.value = 2
                    Log.d("NFCViewModel", "NFC Tag discovery failed")
                }
            }
        }
    }

    fun checkIfNfcIsEnabled() {
        if (isNfcSupported.value) {
            isNfcEnabled.value = nfcAdapter?.isEnabled ?: false
            if (isNfcEnabled.value) {
                NFCController.value = 0
                startNfcListening()
            } else {
                NFCController.value = 2
                stopNfcListening()
                showEnableNfcDialog.value = true
            }
        }
    }

    fun enableNfc() {
        activity?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                it.startActivity(intent)
                Toast.makeText(it, "Lütfen NFC'yi etkinleştirin.", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                it.startActivity(intent)
                Toast.makeText(it, "Lütfen NFC'yi etkinleştirin.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun queueMessage(message: () -> Unit) {
        viewModelScope.launch {
            delay(500) // Her mesaj arasında 500ms gecikme
            message()
        }
    }
}
