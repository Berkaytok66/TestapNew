package com.mebubilisim.trtoolspro.WifiAndBLTestfile

import ServerDataRepository
import SocketServer
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.mebubilisim.trtoolspro.Class.PageState
import com.mebubilisim.trtoolspro.Class.iptalAndNextClass
import com.mebubilisim.trtoolspro.MainActivity
import com.mebubilisim.trtoolspro.R
import com.mebubilisim.trtoolspro.SountTestPages.SoundTestPageActivity
import com.mebubilisim.trtoolspro.WifiAndBLTestfile.ui.theme.TrtoolsproTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GpsTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        queueMessage{
            SocketServer.sendMessageToAllClients(
                functionName = "GpsTestActivity",
                progress = 0
            )
        }
        setContent {
            TrtoolsproTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: LocationViewModel = viewModel()
                    GpsTestScreen(viewModel,this)
                    GpsLifecycleHandler(viewModel)
                }
            }
        }
    }
}

@Composable
fun GpsLifecycleHandler(viewModel: LocationViewModel) {
    DisposableEffect(Unit) {
        viewModel.fetchLocation()
        onDispose {
            // Clean up if necessary
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun GpsTestScreen(viewModel: LocationViewModel = viewModel(),context : Context) {
    val gpsController by viewModel.GPSController
    val coroutineScope = rememberCoroutineScope()


    val pageController by ServerDataRepository.pageController.collectAsState()
    LaunchedEffect(pageController) {
        if (pageController == 2 || pageController == 3) {
            iptalAndNextClass(context, MainActivity::class.java, SoundTestPageActivity::class.java, "ScreenBrightnessTestActivity")
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
                text = "Gps Testi",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        val location by viewModel.currentLocation.collectAsState()
        when (gpsController) {
            0 -> {
                CircularProgressIndicator(color = Color.Gray)
                queueMessage{
                    SocketServer.sendMessageToAllClients(
                        functionName = "GpsTestActivity",
                        progress = 40
                    )
                }
            }
            1 -> {
                location?.let {
                    var position = LatLng(it.latitude, it.longitude)
                    val cameraPositionState = rememberCameraPositionState {
                        position = position
                    }
                    LaunchedEffect(position) {
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(position, 15f))
                    }
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        cameraPositionState = cameraPositionState
                    ) {
                        Marker(
                            state = rememberMarkerState(position = position),
                            title = "Mevcut Konum",
                            icon = bitmapDescriptorFromVector(context, R.drawable.marker)
                        )
                    }
                }
                queueMessage{
                    SocketServer.sendMessageToAllClients(
                        functionName = "GpsTestActivity",
                        progress = 70
                    )
                }
            }
            2 -> {
                Text("GPS Devre Dışı", color = Color.Red, fontSize = 18.sp)
                queueMessage{
                    SocketServer.sendMessageToAllClients(
                        success = false,
                        functionName = "GpsTestActivity",
                        progress = 100
                    )
                }
                PageState(context, 1, 12, SoundTestPageActivity::class.java, MainActivity::class.java)
            }
        }

        if (gpsController == 0) {
            Text(
                text = "Lütfen bekleyin, GPS arayüzünü kontrol ediyoruz...",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    viewModel.GPSController.value = 1
                    queueMessage{
                        SocketServer.sendMessageToAllClients(
                            success = true,
                            functionName = "GpsTestActivity",
                            progress = 100
                        )
                    }
                    coroutineScope.launch {
                        delay(500)
                        // Proceed to next screen
                        PageState(context, 1, 12, SoundTestPageActivity::class.java, MainActivity::class.java)

                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RectangleShape // Dikdörtgen şekil için
            ) {
                Text("Konum Doğru", color = Color.White)
            }

            Button(
                onClick = {
                    viewModel.GPSController.value = 2
                    queueMessage{
                        SocketServer.sendMessageToAllClients(
                            success = false,
                            functionName = "GpsTestActivity",
                            progress = 100
                        )
                    }
                    coroutineScope.launch {
                        delay(500)
                        // Proceed to next screen
                        PageState(context, 1, 12, SoundTestPageActivity::class.java, MainActivity::class.java)

                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RectangleShape // Dikdörtgen şekil için
            ) {
                Text("Konum Yanlış", color = Color.White)
            }
        }
    }
}

fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
    vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
private fun queueMessage(message: () -> Unit) {
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    coroutineScope.launch {
        delay(100) // Her mesaj arasında 500ms gecikme
        message()
    }
}
