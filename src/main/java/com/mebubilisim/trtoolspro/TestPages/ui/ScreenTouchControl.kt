package com.mebubilisim.trtoolspro.TestPages.ui

import SocketServer
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mebubilisim.trtoolspro.Class.PageState
import com.mebubilisim.trtoolspro.Class.iptalAndNextClass
import com.mebubilisim.trtoolspro.MainActivity
import com.mebubilisim.trtoolspro.TestPages.ui.ui.theme.TrtoolsproTheme
import kotlinx.coroutines.delay

var timer: CountDownTimer? = null
var globalList: MutableList<Int> = mutableListOf()

class ScreenTouchControl : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SocketServer.sendMessageToAllClients(statusReason = "Touch test Started...", functionName = "ScrenTouchControl", progress = 0)
        setContent {
            TrtoolsproTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScreenTouchControler()
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // Ses düğmesine basıldığında Toast mesajı göster
            timer?.cancel()
            SocketServer.sendMessageToAllClients(success = false, statusReason = "volume key pressed", functionName = "ScreenTouchControl", progress = 100)
            navigateToNextPage()
            return true  // Event'i tüketildi olarak işaretle
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun navigateToNextPage() {
        PageState(this, 1, 4, DoubleTouchScreenControlActivity::class.java, MainActivity::class.java)
        finish()
    }

    override fun onStop() {
        super.onStop()
        timer?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    TrtoolsproTheme {
        ScreenTouchControler()
    }
}

@Composable
fun ScreenTouchControler() {
    val rows = 20
    val cols = 10
    var activeSquares by remember { mutableStateOf(List(rows * cols) { true }) }
    val touchControl = remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp - 56.dp // Adjust for status bar height
    val squareSize = remember { (screenWidth / cols).coerceAtMost(screenHeight / rows) }
    val localDensity = LocalDensity.current
    val screenWidthPx = with(localDensity) { screenWidth.toPx() }
    val squareSizePx = screenWidthPx / cols
    var remainingTime by remember { mutableStateOf(30) } // 30 saniye olarak başlayacak
    val context = LocalContext.current
    val activity = context as? Activity
    FullScreenModifier(activity = activity)
    LaunchedEffect(key1 = "timer") {
        timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = (millisUntilFinished / 1000).toInt() // Kalan süreyi saniye olarak güncelle
            }

            override fun onFinish() {
                globalList = activeSquares.withIndex().filter { it.value }.map { it.index }.toMutableList()
                SocketServer.sendMessageToAllClients(success = false, statusReason = "Time Error", progress = 100, functionName = "ScreenTouchControl")
                remainingTime = 0 // Süre bittiğinde 0'a ayarla
                PageState(context,1,4,DoubleTouchScreenControlActivity::class.java, MainActivity::class.java)
            }
        }.start()
    }
    DisposableEffect(key1 = "dispose") {
        onDispose {
            timer?.cancel()
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val offsetWidth = (maxWidth - squareSize * cols) / (cols + 1)
        val offsetHeight = (maxHeight - squareSize * rows) / (rows + 1)
        Column(
            verticalArrangement = Arrangement.spacedBy(offsetHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val clickPosition = Offset(x = offset.x, y = offset.y)
                            val (columnIndex, rowIndex) = calculateIndexFromPoint(
                                clickPosition,
                                squareSizePx,
                                cols,
                                rows
                            )
                            val startIndex = rowIndex * cols + columnIndex.coerceIn(0, cols - 1)
                            activeSquares = activeSquares
                                .toMutableList()
                                .apply { this[startIndex] = false }
                            touchControl.value = true // Dokunma kontrolünü başlat
                            globalList =
                                activeSquares
                                    .withIndex()
                                    .filter { it.value }
                                    .map { it.index }
                                    .toMutableList()
                        },
                        onDrag = { change, _ ->
                            change.consumeAllChanges()
                            val currentPosition = change.position
                            val (columnIndex, rowIndex) = calculateIndexFromPoint(
                                currentPosition,
                                squareSizePx,
                                cols,
                                rows
                            )
                            val currentIndex = rowIndex * cols + columnIndex.coerceIn(0, cols - 1)
                            activeSquares = activeSquares
                                .toMutableList()
                                .apply { this[currentIndex] = false }
                            globalList =
                                activeSquares
                                    .withIndex()
                                    .filter { it.value }
                                    .map { it.index }
                                    .toMutableList()

                            when (globalList.size) {
                                200 -> {
                                    SocketServer.sendMessageToAllClients(statusReason = "Screen test in progress", functionName = "ScreenTouchControl", progress = 0)
                                }
                                150 -> {
                                    SocketServer.sendMessageToAllClients(statusReason = "Screen test in progress", functionName = "ScreenTouchControl", progress = 25)
                                }
                                100 -> {
                                    SocketServer.sendMessageToAllClients(statusReason = "Screen test in progress", functionName = "ScreenTouchControl", progress = 50)
                                }
                                50 -> {
                                    SocketServer.sendMessageToAllClients(statusReason = "Screen test in progress", functionName = "ScreenTouchControl", progress = 75)
                                }
                                0 -> {
                                    SocketServer.sendMessageToAllClients(success = true, statusReason = "Screen Test successful", functionName = "ScreenTouchControl", progress = 100)
                                    timer?.cancel()
                                    remainingTime = 0 // Süre bittiğinde 0'a ayarla
                                    PageState(context,1,4,DoubleTouchScreenControlActivity::class.java, MainActivity::class.java)
                                }
                            }
                        },
                        onDragEnd = {
                            touchControl.value = false // Dokunma ve sürüklemeyi sonlandır
                        }
                    )
                }
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
                                    if (activeSquares[index]) Color(0x6170F313) else Color.Transparent,
                                    RectangleShape
                                )
                                .border(1.dp, Color(0x3CFFFFFF), RectangleShape)
                        )
                    }
                }
            }
        }
    }
    TimeText(remainingTime, touchControl.value, context)
}

fun calculateIndexFromPoint(point: Offset, squareSizePx: Float, cols: Int, rows: Int): Pair<Int, Int> {
    val x = point.x
    val y = point.y
    val columnIndex = (x / squareSizePx).toInt().coerceIn(0, cols - 1)
    val rowIndex = (y / squareSizePx).toInt().coerceIn(0, rows - 1)
    return Pair(columnIndex, rowIndex)
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun TimeText(timerr: Int, dokunmaKontrol: Boolean, context: Context) {
    var Bosluk = LocalContext.current.resources.displayMetrics.heightPixels / 8f
    val backgroundColorinfo1endinfo3 = if (dokunmaKontrol) Color(0x3BCCCCCC) else Color(0xB2F4F4F4)
    val info2backgroundColor = if (dokunmaKontrol) Color(0x3BCCCCCC) else Color(0xB9FFADAD)
    val procressBarColorBackgrount = if (dokunmaKontrol) Color(0x3BCCCCCC) else Color(0xFF6A8B89)
    val procressBarColor = if (dokunmaKontrol) Color(0x80706F6F) else Color(0xFF31C911)

    var timer by remember { mutableStateOf(30) }
    timer = timerr
    val progress = remember(timer) { timer / 30f }
    LaunchedEffect(timer) {
        if (timer > 0) {
            delay(1000) // 1 saniye beklet
            timer--
        }
    }
    iptalAndNextClass(context, MainActivity::class.java, DoubleTouchScreenControlActivity::class.java, "ScreenTouchControl")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(30.dp)) // boşluk
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColorinfo1endinfo3) // Arka plan rengi
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
                    text = "Dokunmatik ekran Testini tamamlamak için tüm kutulara dokunun",
                    color = Color.Black
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(info2backgroundColor) // Arka plan rengi
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
                    text = "Dokunmatik alanlar siyaha dönmelidir.",
                    color = Color.Black
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColorinfo1endinfo3) // Arka plan rengi
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
            Spacer(modifier = Modifier.height(Bosluk.dp)) // Divider ve Text arasında boşluk
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(procressBarColorBackgrount)
                    .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Kalan Süre: $timer saniye",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                LinearProgressIndicator(
                    progress = progress,
                    color = procressBarColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .width(18.dp)
                        .background(Color(0xFF06F1F1))
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    com.mebubilisim.trtoolspro.ui.theme.TrtoolsproTheme {
        ScreenTouchControl()
        //  com.mebubilisim.trtoolspro.TestPages.TimeText()
    }
}

@Composable
fun FullScreenModifier(activity: Activity?) {
    activity?.let {
        DisposableEffect(key1 = it) {
            val decorView = it.window.decorView
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )

            onDispose {
                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            }
        }
    }
}
