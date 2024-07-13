package com.mebubilisim.trtoolspro.WifiAndBLTestfile

import ServerDataRepository
import SocketServer
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mebubilisim.trtoolspro.Class.PageState
import com.mebubilisim.trtoolspro.Class.iptalAndNextClass
import com.mebubilisim.trtoolspro.MainActivity
import com.mebubilisim.trtoolspro.R
import com.mebubilisim.trtoolspro.WifiAndBLTestfile.ui.theme.TrtoolsproTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WifiTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sendSocketMessage {
            SocketServer.sendMessageToAllClients(
                functionName = "WifiTestActivity",
                progress = 0
            )
        }

        setContent {
            TrtoolsproTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WifiTestScreen()
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun WifiTestScreen(viewModel: WifiViewModel = viewModel()) {
    val wifiController by viewModel.wifiController
    val wifiOnOfController by viewModel.wifiOnOfController
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()



    LaunchedEffect(Unit) {
        viewModel.scanWifiNetworks()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header section
        Box(
            modifier = Modifier
                .background(Color(0xFF4B5563))
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Wifi Test",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bu test, açık olduğu sürece cihazınızın Wi-Fi bağlantısının çalışıp çalışmadığını anında doğrulayabilir.",
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            wifiOnOfController -> {
                CircularProgressIndicator(color = Color.Gray)
            }
            wifiController == 0 -> {
                CircularProgressIndicator(color = Color.Gray)

                sendSocketMessage {
                    SocketServer.sendMessageToAllClients(
                        functionName = "WifiTestActivity",
                        progress = 50
                    )
                }
            }
            wifiController == 1 -> {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.wifi_succues),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.Green)
                    )
                }

                sendSocketMessage {
                    SocketServer.sendMessageToAllClients(
                        success = true,
                        functionName = "WifiTestActivity",
                        progress = 100
                    )
                }

                coroutineScope.launch {
                    delay(2000)
                    // Yeni sayfaya geçiş işlemi
                    PageState(context, 1, 9, BluetoothTestActivity::class.java, MainActivity::class.java)
                    return@launch
                }
            }
            wifiController == 2 -> {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.wifi_error),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.Red)
                    )
                }

                sendSocketMessage {
                    SocketServer.sendMessageToAllClients(
                        success = false,
                        functionName = "WifiTestActivity",
                        progress = 100
                    )
                }
            }
        }
        val pageController by ServerDataRepository.pageController.collectAsState()
        LaunchedEffect(pageController) {
            if (pageController == 2 || pageController == 3) {
                iptalAndNextClass(context, MainActivity::class.java, BluetoothTestActivity::class.java, "ScreenBrightnessTestActivity")
            }
        }
        if (wifiController == 0 || wifiOnOfController) {
            Text(
                text = if (wifiOnOfController) "WiFi'nin açılmasını bekliyoruz..." else "Lütfen bekleyin, Wi-Fi arayüzünü kontrol ediyoruz...",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                sendSocketMessage {
                    SocketServer.sendMessageToAllClients(
                        success = true,
                        functionName = "WifiTestActivity",
                        message = "Your WiFi seems disabled please check the device",
                        progress = 100
                    )
                }
                coroutineScope.launch {
                    delay(1000)
                    // Yeni sayfaya geçiş işlemi
                    PageState(context, 1, 9, BluetoothTestActivity::class.java, MainActivity::class.java)
                    return@launch
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .height(48.dp),
            shape = RectangleShape // Dikdörtgen şekil için
        ) {
            Text("Wifi Çalışmıyor!", color = Color.White)
        }
    }

    if (wifiOnOfController) {
        sendSocketMessage {
            SocketServer.sendMessageToAllClients(
                functionName = "WifiTestActivity",
                message = "Your WiFi seems disabled please check the device",
                progress = 80
            )
        }

        AlertDialog(
            onDismissRequest = { },
            title = { Text("WiFi") },
            text = { Text("WiFi'niz devre dışı görünüyor, etkinleştirmek istiyor musunuz?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.enableWifi(context)
                }) {
                    Text("Evet")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.wifiOnOfController.value = false
                    viewModel.wifiController.value = 2

                    sendSocketMessage {
                        SocketServer.sendMessageToAllClients(
                            success = false,
                            functionName = "WifiTestActivity",
                            message = "Wifi Not Turned On",
                            progress = 100
                        )
                    }

                    coroutineScope.launch {
                        delay(1000)
                        // Yeni sayfaya geçiş işlemi
                        PageState(context, 1, 9, BluetoothTestActivity::class.java, MainActivity::class.java)
                        return@launch
                    }
                }) {
                    Text("Hayır")
                }
            }
        )
    }
}

private fun sendSocketMessage(message: () -> Unit) {
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    coroutineScope.launch {
        try {
            delay(200) // Her mesaj arasında 500ms gecikme
            message()
        } catch (e: Exception) {
            // Hata mesajını logla veya göster
            Log.e("WifiTestScreen", "Error in sendSocketMessage: ${e.message}", e)
        }
    }
}

