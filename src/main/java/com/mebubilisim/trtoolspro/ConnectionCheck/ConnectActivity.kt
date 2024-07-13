package com.mebubilisim.trtoolspro.ConnectionCheck

import ServerDataRepository
import SocketServer
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ehsanmsz.mszprogressindicator.progressindicator.BallClipRotateMultipleProgressIndicator
import com.ehsanmsz.mszprogressindicator.progressindicator.PacmanProgressIndicator
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
import com.mebubilisim.trtoolspro.ConnectionCheck.ui.theme.TrtoolsproTheme
import com.mebubilisim.trtoolspro.EquipmentPage.KeyEventTestActivity
import com.mebubilisim.trtoolspro.MainActivity
import com.mebubilisim.trtoolspro.R
import com.mebubilisim.trtoolspro.WifiAndBLTestfile.LocationViewModel
import com.mebubilisim.trtoolspro.WifiAndBLTestfile.NFCViewModel
import com.mebubilisim.trtoolspro.WifiAndBLTestfile.WifiViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConnectActivity : ComponentActivity() {
    private lateinit var viewModel: BatteryViewModel
    //private lateinit var WifiView: WifiViewModel
    private lateinit var NFCView: NFCViewModel

    private lateinit var locationViewModel: LocationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BatteryViewModel::class.java)
      //  WifiView = ViewModelProvider(this).get(WifiViewModel::class.java)
        // ViewModel'i initialize edin ve Activity referansını set edin
        NFCView = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(
            NFCViewModel::class.java)
        NFCView.setActivity(this)
        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        lifecycleScope.launch {
            delay(2000) // 2 saniye bekle
            SocketServer.sendMessageToAllClients(statusReason = "Connection test started", functionName = "ConnectActivity", progress = 0)
        }

        setContent {
            TrtoolsproTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SpeakerTestContent(viewModel, NFCView, locationViewModel, ServerDataRepository.messages, this)
                    // BatteryStatus(viewModel)
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

    @Composable
    fun SpeakerTestContent(viewModel: BatteryViewModel, NFCView: NFCViewModel, GPSView: LocationViewModel, messagesFlow: StateFlow<String>, context: Context) {
        val message = messagesFlow.collectAsState().value
        val pageController by ServerDataRepository.pageController.collectAsState()
        LaunchedEffect(pageController) {
            if (pageController == 2 || pageController == 3) {
                iptalAndNextClass(context, MainActivity::class.java, KeyEventTestActivity::class.java, "DoubleTouchScreenControlActivity")
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Bu test, ses dalgalarını kullanarak, cihazınızın mikrofonlarının ve hoparlörlerinin işlevselliklerini aralarında veri iletip otomatik olarak doğrular.",
                color = Color.Black,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp))

            Spacer(modifier = Modifier.height(20.dp))
            JarjComposable(viewModel)
            Divider()
            Text(text = "Bağlantı Testleri")
            BlAndWifiComposable(viewModel, NFCView, GPSView, context)
            when (message) {
                "wirlesChanged" -> {
                    viewModel.setWirelessChargingEnabled(true)
                }
                "CloseWirlesChanged" -> {
                    viewModel.setWirelessChargingEnabled(false)
                }
                "CloseNFC" -> {
                    queueMessage {
                        SocketServer.sendMessageToAllClients(success = false, statusReason = "NFC test Canceled", functionName = "NFCViewModel", progress = 100)
                    }
                    NFCView.NFCController.value = 2
                }
                "ErrorNFC" -> {
                    queueMessage {
                        SocketServer.sendMessageToAllClients(success = false, statusReason = "Error NFC", functionName = "NFCViewModel", progress = 100)
                    }
                    NFCView.NFCController.value = 2
                }
            }
        }
    }

    @Composable
    fun JarjComposable(viewModel: BatteryViewModel) {
        val isWirelessCharging = viewModel.wirlesChanged.collectAsState()
        viewModel.discoverDevices()
        Card(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                jarjTestItem(text = "Jarj Kablosunu Takın", viewModel.kabloluJarjController.value)

                if (isWirelessCharging.value) {
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color.White)
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Burada kablosuz şarj aktif olduğunda gösterilecek UI bileşeni
                            jarjTestItem(text = "Kablosuz Jarj Edin", viewModel.WirlessJarjController.value)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun jarjTestItem(text: String, testState: Int) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                when (testState) {
                    0 -> {
                        BallClipRotateMultipleProgressIndicator()
                    }
                    1 -> Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Completed",
                        tint = Color.Green,
                        modifier = Modifier.size(24.dp)
                    )
                    2 -> Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Failed",
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun BlAndWifiComposable(viewModel: BatteryViewModel, NFCView: NFCViewModel, GPSView: LocationViewModel, context: Context) {
        viewModel.discoverDevices()
     //   if (!WifiView.wifiEnabled.collectAsState().value) {
     //       WifiOpenKontrolDialog(WifiView.wifiOnOfController, context = context, WifiView)
     //   }

        Log.d("BL nin Acık ve Kapalı Olma Durumu", ": ${viewModel.bluetoothEnabled.collectAsState().value}")
        Log.d("BL nin Acık olma durumunda Bulduğu ağ listesi", ": ${viewModel.discoveredDevices.collectAsState().value}")
     //   Log.d("Wifi Acık ve Kapalı Olma Durumu", ": ${WifiView.wifiEnabled.collectAsState().value}")
     //   Log.d("Wifi nin Acık olma durumunda Bulduğu ağ listesi", ": ${WifiView.availableNetworks.collectAsState().value}")

        Card(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                BlWifiNFCGPSTestItem(text = "Bluetooth", viewModel.BLController.value)
             //   BlWifiNFCGPSTestItem(text = "Wifi", WifiView.wifiController.value)
                if (NFCView.isNfcSupported.value) {
                    BlWifiNFCGPSTestItem(text = "NFC", NFCView.NFCController.value)
                    if (NFCView.NFCController.value == 1 || NFCView.NFCController.value == 2) {
                        BlWifiNFCGPSTestItem(text = "GPS (Konum)", GPSView.GPSController.value)
                        LaunchedEffect(Unit) {
                            delay(2000) // 2 saniye beklet
                            GPSView.GpsStartController.value = true // Dialog'u göstermek için  true yap
                        }
                        GpsKontrolDialog(GPSView.GpsStartController, context, GPSView)
                    }
                } else {
                    BlWifiNFCGPSTestItem(text = "GPS (Konum)", GPSView.GPSController.value)
                    LaunchedEffect(Unit) {
                        delay(2000) // 2 saniye beklet
                        GPSView.GpsStartController.value = true // Dialog'u göstermek için  true yap
                    }
                    GpsKontrolDialog(GPSView.GpsStartController, context, GPSView)
                }
            }
        }
    }

    @Composable
    fun BlWifiNFCGPSTestItem(text: String, testState: Int) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                when (testState) {
                    0 -> {
                        PacmanProgressIndicator()
                    }
                    1 -> Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Completed",
                        tint = Color.Green,
                        modifier = Modifier.size(24.dp)
                    )
                    2 -> Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Failed",
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    //Jack Kontrol Girişi
    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun WifiOpenKontrolDialog(showModalBottomSheet: MutableState<Boolean>, context: Context, WifiView: WifiViewModel) {
        val scope = rememberCoroutineScope()
        var skipPartially by remember { mutableStateOf(true) }

        if (showModalBottomSheet.value)
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        showModalBottomSheet.value = false
                        skipPartially = !skipPartially
                        delay(500L)
                        showModalBottomSheet.value = true
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
                            "Cihazınızın Wi-Fi ayarı kapalı ve yüksek bir Android sürümüne sahipsiniz. Wi-Fi testinin tamamlanabilmesi için lütfen ayarlara gidin ve Wi-Fi'ı açın.",
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
                                // İlk buton tıklama işlevi
                                lifecycleScope.launch {
                                    WifiView.wifiEnabled.collect { isEnabled ->
                                        if (!isEnabled) {
                                            WifiView.enableWifi(context)
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                            modifier = Modifier
                                .weight(1f) // Ekrandaki alanın yarısını kapla
                                .padding(top = 16.dp), // Üstten boşluk ekle
                            shape = RoundedCornerShape(8.dp) // Köşeleri yuvarlat
                        ) {
                            Text(text = "Wifi Aç")
                        }

                        // İkinci Buton
                        Button(
                            onClick = {
                                // İkinci buton tıklama işlevi
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
                            Text(text = "İptal")
                        }
                    }
                }
            }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun GpsKontrolDialog(showModalBottomSheet: MutableState<Boolean>, context: Context, GPSView: LocationViewModel) {
        val scope = rememberCoroutineScope()
        var skipPartially by remember { mutableStateOf(true) }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        LaunchedEffect(key1 = showModalBottomSheet.value) {
            if (showModalBottomSheet.value) {
                GPSView.fetchLocation()
            }
        }

        if (showModalBottomSheet.value) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {
                    scope.launch {
                        showModalBottomSheet.value = false
                        skipPartially = !skipPartially
                        delay(500L)
                        showModalBottomSheet.value = true
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
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Konumunuz Doğru mu? Konum bilgisini almak için internet erişiminizin olması gerek geçerek testi atlayabilirsiniz.",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val location by GPSView.currentLocation.collectAsState()

                    location?.let {
                        var position = LatLng(it.latitude, it.longitude)
                        val cameraPositionState = rememberCameraPositionState {
                            position = position
                        }
                        val markerState = rememberMarkerState(position = position)
                        LaunchedEffect(position) {
                            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(position, 15f))
                        }
                        GoogleMap(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            cameraPositionState = cameraPositionState
                        ) {
                            Marker(
                                state = markerState,
                                title = "Mevcut Konum",
                                icon = bitmapDescriptorFromVector(context, R.drawable.marker)
                            )
                        }
                    } ?: run {
                        Text("Konum alınıyor...")
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
                                // İlk buton tıklama işlevi
                                lifecycleScope.launch {
                                    GPSView.GPSController.value = 1
                                    scope.launch {
                                        showModalBottomSheet.value = false
                                    }
                                }
                                queueMessage {
                                    SocketServer.sendMessageToAllClients(success = true, statusReason = "GPS Successful", message = "All Test Finish", functionName = "LocationViewModel", progress = 100)
                                }
                                PageState(context, 0, 1,
                                    MainActivity::class.java,
                                    MainActivity::class.java)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                            modifier = Modifier
                                .weight(1f) // Ekrandaki alanın yarısını kapla
                                .padding(top = 16.dp), // Üstten boşluk ekle
                            shape = RoundedCornerShape(8.dp) // Köşeleri yuvarlat
                        ) {
                            Text(text = "Başarılı")
                        }

                        // İkinci Buton
                        Button(
                            onClick = {
                                GPSView.GPSController.value = 2
                                // İkinci buton tıklama işlevi
                                scope.launch {
                                    showModalBottomSheet.value = false
                                }
                                queueMessage {
                                    SocketServer.sendMessageToAllClients(success = true, statusReason = "GPS Error", message = "All Test Finish", functionName = "LocationViewModel", progress = 100)
                                }
                                PageState(context, 0, 9,
                                    MainActivity::class.java,
                                    MainActivity::class.java)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier
                                .weight(1f) // Ekrandaki alanın yarısını kapla
                                .padding(top = 16.dp), // Üstten boşluk ekle
                            shape = RoundedCornerShape(8.dp) // Köşeleri yuvarlat
                        ) {
                            Text(text = "Geç")
                        }
                    }
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
        lifecycleScope.launch {
            delay(500) // Her mesaj arasında 500ms gecikme
            message()
        }
    }
}
