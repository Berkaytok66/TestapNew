@file:OptIn(ExperimentalComposeUiApi::class)

package com.mebubilisim.trtoolspro.TestPages.ui

import ServerDataRepository
import SocketServer
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.mebubilisim.trtoolspro.Class.PageState
import com.mebubilisim.trtoolspro.Class.iptalAndNextClass
import com.mebubilisim.trtoolspro.MainActivity
import com.mebubilisim.trtoolspro.TestPages.ui.ui.theme.TrtoolsproTheme


class DoubleTouchScreenControlActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)  // Full-screen modu etkinleştir
        SocketServer.sendMessageToAllClients(statusReason  = "Double Touch test has started", functionName = "DoubleTouchScreenControlActivity", progress = 0)

        setContent {
            TrtoolsproTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DoubleTouchScreenControl()
                }
            }
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // Ses düğmesine basıldığında Toast mesajı göster
            SocketServer.sendMessageToAllClients(success = false, statusReason = "Canceled by pressing the volume key", functionName = "DoubleTouchScreenControlActivity", progress = 100)
            PageState(this,1,5,DeadPixelScreenTestActivity::class.java, MainActivity::class.java)

            return true  // Event'i tüketildi olaırak işaretle
        }
        return super.onKeyDown(keyCode, event)
    }
}


@Composable
fun DoubleTouchScreenControl() {

    val rows = 20
    val cols = 10

    var activeSquares by remember { mutableStateOf(List(rows * cols) { true }) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp - 56.dp // Adjust for status bar height
    val squareSize = remember { (screenWidth / cols).coerceAtMost(screenHeight / rows) }

    val context = LocalContext.current
    val activity = context as? Activity
    FullScreenModifier(activity = activity)
    val pageController by ServerDataRepository.pageController.collectAsState()
    LaunchedEffect(pageController) {
        if (pageController == 2 || pageController == 3) {
            iptalAndNextClass(context, MainActivity::class.java, DeadPixelScreenTestActivity::class.java, "DoubleTouchScreenControlActivity")

        }
    }


    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        var touchCount = 0
                        var touchPositions = mutableListOf<String>()
                        do {
                            val event = awaitPointerEvent()
                            event.changes.forEach { change ->
                                if (change.pressed && !change.previousPressed) {
                                    touchCount++
                                    touchPositions.add(change.position.toString())
                                } else if (!change.pressed && change.previousPressed) {
                                    touchCount--
                                }
                            }
                            if (touchCount >= 2) {
                               // Log.d("MultiTouch", "Number of fingers down: $touchCount")
                               // Log.d("MultiTouch", "Positions: ${touchPositions.joinToString()}")
                                SocketServer.sendMessageToAllClients(success = true, statusReason = "Double Touch test Passed", functionName = "DoubleTouchScreenControlActivity", progress = 100)

                                touchPositions.clear()
                                PageState(context,1,5,DeadPixelScreenTestActivity::class.java,MainActivity::class.java)
                                return@awaitPointerEventScope
                            }
                        } while (touchCount > 0)

                    }
                }
            }

    ) {
        val offsetWidth = (maxWidth - squareSize * cols) / (cols + 1)
        val offsetHeight = (maxHeight - squareSize * rows) / (rows + 1)
        Column(
            verticalArrangement = Arrangement.spacedBy(offsetHeight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (i in 0 until rows) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(offsetWidth),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (j in 0 until cols) {
                        val index = i * cols + j
                        Box(
                            modifier = Modifier
                                .size(squareSize)
                                .background(
                                    if (activeSquares[index]) Color.Green else Color.Transparent,
                                    RectangleShape
                                )
                                .border(1.dp, Color.White, RectangleShape)
                        )
                    }
                }
            }
        }
    }
    TimeText2(context)

}
@Composable
fun TimeText2(context : Context) {



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 5.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(30.dp)) // boşluk
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Arka plan rengi
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info, // İkon olarak yıldız kullanıldı
                    contentDescription = "Icon",
                    tint = Color.Blue,
                    modifier = Modifier.padding(end = 8.dp) // İkon ile metin arasında boşluk bırak
                )
                Text(
                    text = "Cift Dokunma Testini tamamlamak için Ekrana İki Parmagınızla Aynı Anda Basın",
                    color = Color.Black
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Arka plan rengi
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Icon",
                    tint = Color.Blue,
                    modifier = Modifier.padding(end = 8.dp) // İkon ile metin arasında boşluk bırak
                )
                Text(
                    text = "Testi başarısız kılmak için ses kısma tuşuna basın",
                    color = Color.Black
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    TrtoolsproTheme {
        DoubleTouchScreenControl()
    }
}