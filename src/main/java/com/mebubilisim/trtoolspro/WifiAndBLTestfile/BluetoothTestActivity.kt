package com.mebubilisim.trtoolspro.WifiAndBLTestfile

import ServerDataRepository
import SocketServer
import android.annotation.SuppressLint
import android.os.Bundle
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
import com.mebubilisim.trtoolspro.ConnectionCheck.BluetoothViewModel
import com.mebubilisim.trtoolspro.MainActivity
import com.mebubilisim.trtoolspro.R
import com.mebubilisim.trtoolspro.WifiAndBLTestfile.ui.theme.TrtoolsproTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BluetoothTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            SocketServer.sendMessageToAllClients(
                functionName = "BluetoothTestActivity",
                message = "Bluetooth Test Started",
                progress = 0
            )
        setContent {
            TrtoolsproTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BluetoothTestScreen()
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BluetoothTestScreen(viewModel: BluetoothViewModel = viewModel()) {
    val bluetoothController by viewModel.bluetoothController
    val bluetoothOnOfController by viewModel.bluetoothOnOfController
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.startDiscovery()
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
                text = "Bluetooth Testi",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bu test, açık olduğu sürece cihazınızın Bluetooth'unun çalışıp çalışmadığını anında doğrulayabilir.",
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        queueMessage{
            SocketServer.sendMessageToAllClients(
                functionName = "BluetoothTestActivity",
                message = "Bluetooth test in progress",
                progress = 40
            )
        }
        when {
            bluetoothOnOfController -> {
                CircularProgressIndicator(color = Color.Gray)
            }
            bluetoothController == 0 -> {
                CircularProgressIndicator(color = Color.Gray)


            }
            bluetoothController == 1 -> {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bluethoot_succes),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.Green)
                    )

                }
                queueMessage{
                    SocketServer.sendMessageToAllClients(
                        success = true,
                        functionName = "BluetoothTestActivity",
                        message = "Bluetooth test Successfully",
                        progress = 100
                    )
                }
                coroutineScope.launch {
                    delay(2000)
                    // Yeni sayfaya geçiş işlemi
                    PageState(context, 1, 10, NfcTestActivity::class.java, MainActivity::class.java)
                    return@launch
                }
            }
            bluetoothController == 2 -> {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bluethoot_eror),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.Red)
                    )
                }
            }
        }
        val pageController by ServerDataRepository.pageController.collectAsState()
        LaunchedEffect(pageController) {
            if (pageController == 2 || pageController == 3) {
                iptalAndNextClass(context, MainActivity::class.java, NfcTestActivity::class.java, "ScreenBrightnessTestActivity")
            }
        }
        if (bluetoothController == 0 || bluetoothOnOfController) {
            Text(
                text = if (bluetoothOnOfController) "Bluetooth'un açılmasını bekliyoruz..." else "Lütfen bekleyin, Bluetooth arayüzünü kontrol ediyoruz...",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))


        Button(
            onClick = {
             //  viewModel.startDiscovery()  Testi Tekrarlatır
                queueMessage{
                    SocketServer.sendMessageToAllClients(
                        success = false,
                        functionName = "BluetoothTestActivity",
                        message = "Canceled by User",
                        progress = 100
                    )
                }
                coroutineScope.launch {
                    delay(2000)
                    // Yeni sayfaya geçiş işlemi
                    PageState(context, 1, 10, NfcTestActivity::class.java, MainActivity::class.java)
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
            Text("Bluetooth Çalışmıyor!", color = Color.White)
        }
    }

    if (bluetoothOnOfController) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Bluetooth") },
            text = { Text("Bluetooth'unuz devre dışı görünüyor, etkinleştirmek istiyor musunuz?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.enableBluetooth(context)
                }) {
                    Text("Evet")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.bluetoothOnOfController.value = false
                    queueMessage{
                        SocketServer.sendMessageToAllClients(
                            success = false,
                            functionName = "BluetoothTestActivity",
                            message = "Bluetooth test Error",
                            progress = 100
                        )
                    }
                    coroutineScope.launch {
                        delay(500)
                        // Yeni sayfaya geçiş işlemi
                        PageState(context, 1, 10, NfcTestActivity::class.java, MainActivity::class.java)
                        return@launch
                    }
                }) {
                    Text("Hayır")
                }
            }
        )
    }
}


private fun queueMessage(message: () -> Unit) {
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    coroutineScope.launch {
        delay(100) // Her mesaj arasında 500ms gecikme
        message()
    }
}
