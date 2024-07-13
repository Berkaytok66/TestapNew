package com.mebubilisim.trtoolspro.TestPages.ui

import ServerDataRepository
import SocketServer
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.mebubilisim.trtoolspro.Class.PageState
import com.mebubilisim.trtoolspro.Class.iptalAndNextClass
import com.mebubilisim.trtoolspro.MainActivity
import com.mebubilisim.trtoolspro.TestPages.ui.ui.theme.TrtoolsproTheme
import kotlinx.coroutines.launch

class DeadPixelScreenTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)  // Ensure the app is full-screen
        SocketServer.sendMessageToAllClients(statusReason  ="Dead pixel test has started", functionName = "DeadPixelScreenTestActivity", progress = 0)

        setContent {
            TrtoolsproTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ColorChangerScreen()
                }
            }
        }
    }
}

@Composable
fun ColorChangerScreen() {
    val infiniteTransition = remember { Animatable(0f) }
    var colorState by remember { mutableStateOf(0) }
    val colors = listOf(Color.Green, Color.Blue, Color.White, Color.Red, Color.Black)
    val textColors = listOf(Color.Black, Color.White, Color.Black, Color.White, Color.White)
    val colorNames = listOf("YEŞİL", "MAVİ", "BEYAZ", "KIRMIZI", "SİYAH")
    val bannerVisibleState = remember { mutableStateOf(false) }
    val allowChange = remember { mutableStateOf(true) }
    val context = LocalContext.current
    val activity = context as? Activity

    FullScreenModifier(activity = activity)

    // Animasyon tanımı
    LaunchedEffect(key1 = true) {
        infiniteTransition.animateTo(
            targetValue = 680f, // Maksimum yükseklik farkı
            animationSpec = InfiniteRepeatableSpec(
                animation = tween(3500, easing = LinearEasing), // 1500ms süre
                repeatMode = RepeatMode.Reverse // Yukarı ve aşağı hareket
            )
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                when (colorState){
                    0 -> { SocketServer.sendMessageToAllClients(statusReason = "Green Screen continues", functionName = "DeadPixelScreenTestActivity", progress = 10)}
                    1 -> { SocketServer.sendMessageToAllClients(statusReason = "Blue Screen continues", functionName = "DeadPixelScreenTestActivity", progress = 30)}
                    2 -> { SocketServer.sendMessageToAllClients(statusReason = "White Screen continues", functionName = "DeadPixelScreenTestActivity", progress = 50)}
                    3 -> { SocketServer.sendMessageToAllClients(statusReason = "Red Screen continues", functionName = "DeadPixelScreenTestActivity", progress = 70)}
                    4 -> { SocketServer.sendMessageToAllClients(statusReason = "Black Screen continues", functionName = "DeadPixelScreenTestActivity", progress =90)}
                }
                if (allowChange.value) {
                    colorState = (colorState + 1) % colors.size
                    if (colors[colorState] == Color.Black) {
                        allowChange.value = false
                        bannerVisibleState.value = true
                    }
                } else if (colors[colorState] == Color.Black && !bannerVisibleState.value) {
                    bannerVisibleState.value = true
                }
            },
        color = colors[colorState]
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "${colorNames[colorState]} \n Ekrana tıklayarak rengi değiştirin",
                fontSize = 24.sp,
                color = textColors[colorState],
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = infiniteTransition.value.dp)
            )
        }
    }

    if (bannerVisibleState.value) {
        OpenKontrolDialog(bannerVisibleState, context)
    }
    val pageController by ServerDataRepository.pageController.collectAsState()
    LaunchedEffect(pageController) {
        if (pageController == 2 || pageController == 3) {
            iptalAndNextClass(context, MainActivity::class.java, ScreenBrightnessTestActivity::class.java, "DeadPixelScreenTestActivity")
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
                scope.launch {
                    showModalBottomSheet.value = false
                }
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
                        "Herhangi bir renk solması, ışık kaçağı, ışık sızması, yatay ve ya dikey çizgiler, aydınlık ve ya karanlık patch'ler, ölü pixel, parlak pixel ve ya başka bir hata farkettiniz mi?",
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
                            SocketServer.sendMessageToAllClients(success = false, statusReason ="Dead pixel found", functionName = "DeadPixelScreenTestActivity", progress = 100)
                            PageState(context,1,6,ScreenBrightnessTestActivity::class.java, MainActivity::class.java)
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

                            scope.launch {
                                showModalBottomSheet.value = false
                            }
                            SocketServer.sendMessageToAllClients(success = true, statusReason ="No dead pixels detected", functionName = "DeadPixelScreenTestActivity", progress = 100)

                            PageState(context,1,6,ScreenBrightnessTestActivity::class.java, MainActivity::class.java)
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
