package com.mebubilisim.trtoolspro.WifiAndBLTestfile

import ServerDataRepository
import SocketServer
import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
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

class NfcTestActivity : ComponentActivity() {
    private lateinit var NFCView: NFCViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NFCView = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(
            NFCViewModel::class.java)
        NFCView.setActivity(this)
        queueMessage{
            SocketServer.sendMessageToAllClients(
                functionName = "NfcTestActivity",
                progress = 0
            )
        }
        setContent {
            TrtoolsproTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: NFCViewModel = viewModel()
                    viewModel.setActivity(this@NfcTestActivity)
                    NfcTestScreen(viewModel)
                    NfcLifecycleHandler(viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        NFCView.checkIfNfcIsEnabled()
    }

    override fun onPause() {
        super.onPause()
        NFCView.stopNfcListening()
    }
}

@Composable
fun NfcLifecycleHandler(viewModel: NFCViewModel) {
    DisposableEffect(Unit) {
        viewModel.checkIfNfcIsEnabled()
        onDispose {
            viewModel.stopNfcListening()
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NfcTestScreen(viewModel: NFCViewModel = viewModel()) {
    val nfcController by viewModel.NFCController
    val isNfcSupported by viewModel.isNfcSupported
    val isNfcEnabled by viewModel.isNfcEnabled
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.checkIfNfcIsEnabled()
    }
    val pageController by ServerDataRepository.pageController.collectAsState()
    LaunchedEffect(pageController) {
        if (pageController == 2 || pageController == 3) {
            iptalAndNextClass(context, MainActivity::class.java, NfcTestActivity::class.java, "ScreenBrightnessTestActivity")
        }
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
                text = "Nfc Testi",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bu test, açık olduğu sürece cihazınızın NFC'sinin çalışıp çalışmadığını anında doğrulayabilir.",
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            !isNfcSupported -> {
                Text("NFC Desteklenmiyor", color = Color.Red, fontSize = 18.sp)
            }
            nfcController == 0 -> {
                CircularProgressIndicator(color = Color.Gray)
                queueMessage{
                    SocketServer.sendMessageToAllClients(
                        functionName = "NfcTestActivity",
                        message = "nfc expected",
                        progress = 50
                    )
                }
            }
            nfcController == 1 -> {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nfc_succes),
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
                        functionName = "NfcTestActivity",
                        message = "nfc successfully",
                        progress = 100
                    )
                }
                coroutineScope.launch {
                    delay(1000)
                    // Yeni sayfaya geçiş işlemi
                    PageState(context, 1, 11, GpsTestActivity::class.java, MainActivity::class.java)
                    return@launch
                }
            }
            nfcController == 2 -> {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nfc_error),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.Green)
                    )
                }

            }
        }

        if (nfcController == 0 || !isNfcEnabled) {
            Text(
                text = if (!isNfcEnabled) "NFC'nin açılmasını bekliyoruz..." else "Lütfen bekleyin, NFC arayüzünü kontrol ediyoruz...",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                queueMessage{
                    SocketServer.sendMessageToAllClients(
                        success = false,
                        functionName = "NfcTestActivity",
                        message = "Nfc test Error",
                        progress = 100
                    )
                }
                coroutineScope.launch {
                    delay(1000)
                    // Yeni sayfaya geçiş işlemi
                    PageState(context, 1, 11, GpsTestActivity::class.java, MainActivity::class.java)
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
            Text("Nfc Testi Başarısız", color = Color.White)
        }
    }

    if (viewModel.showEnableNfcDialog.value) {
        AlertDialog(
            onDismissRequest = { viewModel.showEnableNfcDialog.value = false },
            title = { Text("NFC") },
            text = { Text("NFC'niz devre dışı görünüyor, etkinleştirmek istiyor musunuz?") },
            confirmButton = {
                TextButton(onClick = {
                    context.startActivity(Intent(android.provider.Settings.ACTION_NFC_SETTINGS))
                    viewModel.showEnableNfcDialog.value = false
                }) {
                    Text("Evet")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.showEnableNfcDialog.value = false
                    // İstenirse burada iptal işlemleri yapılabilir

                    queueMessage{
                        SocketServer.sendMessageToAllClients(
                            success = false,
                            functionName = "NfcTestActivity",
                            message = "Nfc Test Error",
                            progress = 100
                        )
                    }
                    coroutineScope.launch {
                        delay(1000)
                        // Yeni sayfaya geçiş işlemi
                        PageState(context, 1, 11, GpsTestActivity::class.java, MainActivity::class.java)
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
        delay(100) // Her mesaj arasında 100ms gecikme
        message()
    }
}
