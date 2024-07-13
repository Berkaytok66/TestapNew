package com.mebubilisim.trtoolspro.SountTest

import ServerDataRepository
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mebubilisim.trtoolspro.Class.iptalAndNextClass
import com.mebubilisim.trtoolspro.MainActivity
import com.mebubilisim.trtoolspro.R
import com.mebubilisim.trtoolspro.SountTest.ui.theme.TrtoolsproTheme
import com.mebubilisim.trtoolspro.TestPages.ui.ScreenBrightnessTestActivity

class HandsetTestPage : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrtoolsproTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Ahize Testi",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            },
                            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF4B5563))
                        )
                    },
                    content = {
                        HandsetTestScreen(
                            onStartTest = { playSoundThroughEarPiece(this, R.raw.sound_test) },
                            onStopTest = { stopHandsetTest() },
                            modifier = Modifier.padding(it)
                        )
                    }
                )
            }
        }
    }

    private fun playSoundThroughEarPiece(context: Context, resourceId: Int) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mediaPlayer = MediaPlayer.create(context, resourceId).apply {
            setOnPreparedListener {
                audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
                audioManager.isSpeakerphoneOn = false
                start()
            }
            setOnCompletionListener {
                it.release()
                audioManager.mode = AudioManager.MODE_NORMAL
                audioManager.isSpeakerphoneOn = true
            }
        }
        mediaPlayer?.start()
    }

    private fun stopHandsetTest() {
        mediaPlayer?.release()
        mediaPlayer = null
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = AudioManager.MODE_NORMAL
        audioManager.isSpeakerphoneOn = true
    }

    override fun onDestroy() {
        super.onDestroy()
        stopHandsetTest()
    }
}

@Composable
fun HandsetTestScreen(
    onStartTest: () -> Unit,
    onStopTest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val testStarted = remember { mutableStateOf(false) }
    val pageController by ServerDataRepository.pageController.collectAsState()
    LaunchedEffect(pageController) {
        if (pageController == 2 || pageController == 3) {
            iptalAndNextClass(context, MainActivity::class.java, ScreenBrightnessTestActivity::class.java, "DeadPixelScreenTestActivity")
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(0.dp)
                    .background(Color(0xFF4B5563))
            ) {
                InfoText(text = "Testi başlat buttonuna basarak testi başlatabilirsiniz.")
                InfoText(text = "Sessiz bir ortamda olun.")
                InfoText(text = "Test sırasında ahizeyi kulağınıza yaklaştırın.")
            }

            if (testStarted.value) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()

                ) {
                    Image(
                        painter = painterResource(R.drawable.ahize_image),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,

                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    onStartTest()
                    testStarted.value = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape
            ) {
                Text(text = "Testi Başlat")
            }

            if (testStarted.value) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            onStopTest()
                            testStarted.value = false
                        },
                        modifier = Modifier.weight(1f),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green) // Arka plan rengi burada ayarlanır
                    ) {
                        Text(text = "Başarılı", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.size(16.dp))
                    Button(
                        onClick = {
                            onStopTest()
                            testStarted.value = false
                        },
                        modifier = Modifier.weight(1f),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // Arka plan rengi burada ayarlanır
                    ) {
                        Text(text = "Başarısız")
                    }
                }
            }
        }
    }
}

@Composable
fun InfoText(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFFF9800))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = Color(0xFFFF9800)
        )
    }
}
