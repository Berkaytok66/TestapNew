package com.mebubilisim.trtoolspro.TestPages.ui

import ServerDataRepository
import SocketServer
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.mebubilisim.trtoolspro.Class.PageState
import com.mebubilisim.trtoolspro.Class.iptalAndNextClass
import com.mebubilisim.trtoolspro.MainActivity
import com.mebubilisim.trtoolspro.SountTestPages.SoundTestPageActivity
import com.mebubilisim.trtoolspro.TestPages.ui.ui.theme.TrtoolsproTheme
import com.mebubilisim.trtoolspro.WifiAndBLTestfile.WifiTestActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScreenBrightnessTestActivity : ComponentActivity() {
    private var screenBrightness by mutableStateOf(0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)  // Full-screen modu etkinleştir
        lifecycleScope.launch {
            CoroutineScope(Dispatchers.Default).launch {
                SocketServer.sendMessageToAllClients(statusReason = "Screen brightness test started", functionName = "ScreenBrightnessTestActivity", progress = 0)
                delay(1000) // 2 saniye bekle
                adjustBrightnessGradually(duration = 7000, steps = 50)
            }

        }


        setContent {
            TrtoolsproTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ColorfulScreenWithPercentage()
                }
            }
        }
    }

    private suspend fun adjustBrightnessGradually(duration: Long, steps: Int) {
        val stepDuration = duration / steps
        val brightnessIncrement = 1f / steps  // Increment brightness from 0 to 1 in defined steps
        for (i in 1..steps) {
            val currentBrightness = brightnessIncrement * i
            val brightnessPercentage = (currentBrightness * 100).toInt()  // Convert to percentage
            withContext(Dispatchers.Main) {
                val layoutParams = window.attributes
                layoutParams.screenBrightness = currentBrightness
                window.attributes = layoutParams
                screenBrightness = brightnessPercentage
                SocketServer.sendMessageToAllClients(statusReason = "Continues", functionName = "ScreenBrightnessTestActivity", progress = screenBrightness)
            }
            delay(stepDuration)
        }
    }

    @SuppressLint("UnusedBoxWithConstraintsScope")
    @Composable
    fun ColorfulScreenWithPercentage() {
        val context = LocalContext.current
        val activity = context as? Activity
        val bannerVisibleState = remember { mutableStateOf(false) }
        FullScreenModifier(activity = activity)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    bannerVisibleState.value = true
                }
        ) {
            val colorsList = listOf(
                listOf(Color.Red, Color(0xFFCCCCCC)),
                listOf(Color.Green, Color(0xFFCCCCCC)),
                listOf(Color.Blue, Color(0xFFCCCCCC)),
                listOf(Color.Black, Color(0xFFCCCCCC))
            )
            Column(modifier = Modifier.fillMaxSize()) {
                colorsList.forEach { colors ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(brush = Brush.verticalGradient(colors = colors))
                    )
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().padding(20.dp)
            ) {
                Text(
                    text = "$screenBrightness%",
                    fontSize = 48.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            if (screenBrightness == 100){
                bannerVisibleState.value = true
            }
            if (bannerVisibleState.value) {
                OpenKontrolDialog(bannerVisibleState, context)
            }
            val pageController by ServerDataRepository.pageController.collectAsState()
            LaunchedEffect(pageController) {
                if (pageController == 2 || pageController == 3) {
                    iptalAndNextClass(context, MainActivity::class.java, SoundTestPageActivity::class.java, "ScreenBrightnessTestActivity")
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun OpenKontrolDialog(showModalBottomSheet: MutableState<Boolean>, context: Context) {
        val scope = rememberCoroutineScope()

        if (showModalBottomSheet.value) {
            ModalBottomSheet(
                onDismissRequest = {
                    // Bu kısmı boş bırakarak dışına tıklanınca kapanmasını engelliyoruz
                    showModalBottomSheet.value = false
                },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth() // Tüm genişliği kapla
                            .padding(horizontal = 16.dp), // Yatayda 16 dp padding
                        horizontalAlignment = Alignment.CenterHorizontally // İçerikleri yatayda ortala
                    ) {
                        Text(
                            "Herhangi bir renk solması, ışık kaçağı, ışık sızması, yatay ve ya dikey çizgiler, aydınlık ve ya karanlık patch'ler, ölü pixel, parlak pixel ve ya başka bir hata farkettinizmi ?",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth() // Ekran genişliğini dolduracak şekilde ayarla
                            .padding(16.dp), // Dıştan boşluk ekle
                        horizontalArrangement = Arrangement.spacedBy(16.dp), // Butonlar arası boşluk
                        verticalAlignment = Alignment.CenterVertically // Dikeyde ortala
                    ) {
                        // İlk Buton
                        Button(
                            onClick = {
                                // "Evet" butonu tıklama işlevi
                                SocketServer.sendMessageToAllClients(success = false, message = "Problem detected in Brightness Test", functionName = "ScreenBrightnessTestActivity", progress = 100)
                                PageState(context, 1, 7, WifiTestActivity::class.java, MainActivity::class.java)
                                scope.launch {
                                    showModalBottomSheet.value = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier
                                .weight(1f) // Ekrandaki alanın yarısını kapla
                                .padding(top = 16.dp), // Üstten boşluk ekle
                            shape = RoundedCornerShape(8.dp) // Köşeleri yuvarlat
                        ) {
                            Text(text = "Evet")
                        }

                        // İkinci Buton
                        Button(
                            onClick = {
                                // "Hayır" butonu tıklama işlevi

                                SocketServer.sendMessageToAllClients(success = true, statusReason = "No problem detected", functionName = "ScreenBrightnessTestActivity", progress = 100)
                                PageState(context, 1, 7, WifiTestActivity::class.java, MainActivity::class.java)
                                scope.launch {

                                    showModalBottomSheet.value = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                            modifier = Modifier
                                .weight(1f) // Ekrandaki alanın yarısını kapla
                                .padding(top = 16.dp), // Üstten boşluk ekle
                            shape = RoundedCornerShape(8.dp) // Köşeleri yuvarlat
                        ) {
                            Text(text = "Hayır")
                        }
                    }
                }
            }
        }
    }
}
