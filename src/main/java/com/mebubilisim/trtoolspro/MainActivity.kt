package com.mebubilisim.trtoolspro

import ServerDataRepository
import SocketServer
import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import com.mebubilisim.trtoolspro.Class.navigateAndFinishCurrent
import com.mebubilisim.trtoolspro.ConnectionCheck.ConnectActivity
import com.mebubilisim.trtoolspro.EquipmentPage.KeyEventTestActivity
import com.mebubilisim.trtoolspro.Page.AgreementPage
import com.mebubilisim.trtoolspro.Page.HomePage
import com.mebubilisim.trtoolspro.Page.SettingsPage
import com.mebubilisim.trtoolspro.SountTest.SountTestPage
import com.mebubilisim.trtoolspro.SountTestPages.SoundTestPageActivity
import com.mebubilisim.trtoolspro.TestPages.ui.DeadPixelScreenTestActivity
import com.mebubilisim.trtoolspro.TestPages.ui.DoubleTouchScreenControlActivity
import com.mebubilisim.trtoolspro.TestPages.ui.ScreenBrightnessTestActivity
import com.mebubilisim.trtoolspro.TestPages.ui.ScreenTouchControl
import com.mebubilisim.trtoolspro.WifiAndBLTestfile.BluetoothTestActivity
import com.mebubilisim.trtoolspro.WifiAndBLTestfile.GpsTestActivity
import com.mebubilisim.trtoolspro.WifiAndBLTestfile.NfcTestActivity
import com.mebubilisim.trtoolspro.WifiAndBLTestfile.WifiTestActivity
import com.mebubilisim.trtoolspro.ui.theme.TrtoolsproTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Broadcast Receiver'ı başlat
        SocketServer.startServer(8080)

        setContent {
            TrtoolsproTheme {
                // Tema renkleriyle bir yüzey konteyneri
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                 //       SayfaGecisleri()
                        otoKomut(ServerDataRepository.messages,ServerDataRepository.portMessage)
                    }
                }
            }
        }
    }
}

@Composable
fun SayfaGecisleri(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "MainScreen"){
        composable(route="MainScreen"){
            MainScreen(navController,ServerDataRepository.messages)
        }
        composable(route="TouchResultScreen"){
            //TouchResultScreen(navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TrtoolsproTheme {
        SayfaGecisleri()
    }
}
@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun otoKomut(messagesFlow: StateFlow<String>, portMessagesFlow: StateFlow<String>) {
    val message by messagesFlow.collectAsState()
    val portMessage by portMessagesFlow.collectAsState()
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val deviceInfo = getDeviceInfo(context)
    var currentMessage by remember { mutableStateOf(portMessage) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(message) {
        when (message) {
            "all" -> { navigateAndFinishCurrent(context, ScreenTouchControl::class.java) }
            "screntest" -> { navigateAndFinishCurrent(context, ScreenTouchControl::class.java) }
            "getCiftDokunmatikKontrol" -> { navigateAndFinishCurrent(context, DoubleTouchScreenControlActivity::class.java) }
            "pixelKontrol" -> { navigateAndFinishCurrent(context, DeadPixelScreenTestActivity::class.java) }
            "brightness" -> { navigateAndFinishCurrent(context, ScreenBrightnessTestActivity::class.java) }
            "WifiTest" -> { navigateAndFinishCurrent(context,WifiTestActivity::class.java)}
            "BluetoothTest" -> { navigateAndFinishCurrent(context,BluetoothTestActivity::class.java)}
            "NfcTest" -> { navigateAndFinishCurrent(context,NfcTestActivity::class.java)}
            "GpsTest" -> { navigateAndFinishCurrent(context, GpsTestActivity::class.java)}
            "soundTest" -> { navigateAndFinishCurrent(context, SoundTestPageActivity::class.java) }
            "newSountTest" -> { navigateAndFinishCurrent(context, SountTestPage::class.java)}
            "KeyEventTestActivity" -> { navigateAndFinishCurrent(context, KeyEventTestActivity::class.java) }
            "info" -> {
                SocketServer.sendDeviceInfo(
                    Manufacturer = deviceInfo.manufacturer,
                    Model = deviceInfo.model,
                    Brand = deviceInfo.brand,
                    Device = deviceInfo.device,
                    Product = deviceInfo.product,
                    Hardware = deviceInfo.hardware,
                    Serial = deviceInfo.serial,
                    Android_ID = deviceInfo.androidId,
                    SIM_Country_ISO = deviceInfo.simCountryIso,
                    Network_Operator = deviceInfo.networkOperatorName,
                    MAC_Address = deviceInfo.macAddress,
                    Screen_Width_pixels = deviceInfo.widthPixels.toString(),
                    Screen_Height_pixels = deviceInfo.heightPixels.toString(),
                    Density_DPI = deviceInfo.densityDpi.toString()
                )
            }
        }

        ServerDataRepository.resetMessage()  // Komut işlendikten sonra mesajı sıfırla
    }

    LaunchedEffect(portMessage) {
        coroutineScope.launch {
            while (true) {
                currentMessage = portMessage
                delay(2000)
            }
        }
    }

    Scaffold(
        modifier = Modifier.padding(all = 12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.oto_komut_pag_images),
                    contentDescription = "Blinking Image",
                    modifier = Modifier
                        .size(250.dp)
                        .alpha(alpha)
                )
            }
            Spacer(modifier = Modifier.height(96.dp))

            AnimatedContent(
                targetState = currentMessage,
                transitionSpec = {
                    slideInVertically { height -> height } + fadeIn() with
                            slideOutVertically { height -> -height } + fadeOut()
                }
            ) { targetMessage ->
                Text(
                    text = targetMessage,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavController, messagesFlow: StateFlow<String>) {
    val message = messagesFlow.collectAsState().value

    val navigationBarItems = remember { NavigationBarItems.values() }
    var selectedIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current

    val deviceInfo = getDeviceInfo(context)

    Scaffold(
        modifier = Modifier.padding(all = 12.dp),
        bottomBar = {
            AnimatedNavigationBar(
                selectedIndex = selectedIndex,
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth(),
                cornerRadius = shapeCornerRadius(cornerRadius = 34.dp),
                ballAnimation = Parabolic(tween(400)),
                indentAnimation = Height(tween(300)),
                barColor = MaterialTheme.colorScheme.primary,
                ballColor = MaterialTheme.colorScheme.primary,
            ) {
                navigationBarItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable { selectedIndex = item.ordinal },
                        contentAlignment = Alignment.Center
                    ) {
                        item.icon()
                    }
                }
            }
        }
    ) {
        LaunchedEffect(message) {
            when (message) {
                "all" -> { navigateAndFinishCurrent(context, ScreenTouchControl::class.java, false) }
                "screntest" -> { navigateAndFinishCurrent(context, ScreenTouchControl::class.java, false) }
                "getCiftDokunmatikKontrol" -> { navigateAndFinishCurrent(context, DoubleTouchScreenControlActivity::class.java, false) }
                "pixelKontrol" -> { navigateAndFinishCurrent(context, DeadPixelScreenTestActivity::class.java, false) }
                "brightness" -> { navigateAndFinishCurrent(context, ScreenBrightnessTestActivity::class.java, false) }
                "soundTest" -> { navigateAndFinishCurrent(context, SoundTestPageActivity::class.java, false) }
                "ConnectTest" -> { navigateAndFinishCurrent(context, ConnectActivity::class.java, false) }
                "KeyEventTestActivity" -> { navigateAndFinishCurrent(context, KeyEventTestActivity::class.java, false) }
                "info" -> {
                    SocketServer.sendDeviceInfo(
                        Manufacturer = deviceInfo.manufacturer,
                        Model = deviceInfo.model,
                        Brand = deviceInfo.brand,
                        Device = deviceInfo.device,
                        Product = deviceInfo.product,
                        Hardware = deviceInfo.hardware,
                        Serial = deviceInfo.serial,
                        Android_ID = deviceInfo.androidId,
                        SIM_Country_ISO = deviceInfo.simCountryIso,
                        Network_Operator = deviceInfo.networkOperatorName,
                        MAC_Address = deviceInfo.macAddress,
                        Screen_Width_pixels = deviceInfo.widthPixels.toString(),
                        Screen_Height_pixels = deviceInfo.heightPixels.toString(),
                        Density_DPI = deviceInfo.densityDpi.toString()
                    )
                }
            }
            Log.d("TAGggggggggggg", "${ServerDataRepository.messages.value}")
            ServerDataRepository.resetMessage()  // Komut işlendikten sonra mesajı sıfırla
        }

        when (selectedIndex) {
            0 -> HomePage(onFeatureClick = { index ->
                when (index) {
                    0 -> {
                        ServerDataRepository.pageController.value = 1
                        navigateAndFinishCurrent(context, ScreenTouchControl::class.java)
                    }
                    1 -> {
                        Log.d("IndexClicked", "index 1: $index Tıklandı")
                    }
                }
            })
            1 -> AgreementPage()
            2 -> SettingsPage()
        }
    }
}


enum class NavigationBarItems(val icon: @Composable () -> Unit) {
    Home({ CustomIcon(R.drawable.test_icon) }),
    Call({ CustomIcon(R.drawable.policy_icons) }),
    Settings({ CustomIcon(R.drawable.settings_icon) });
}

@Composable
fun CustomIcon(iconId: Int) {
    Icon(
        painter = painterResource(id = iconId),
        contentDescription = null,
        modifier = Modifier.size(26.dp),
        tint = MaterialTheme.colorScheme.onTertiary
    )
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}
@SuppressLint("ServiceCast", "MissingPermission")
private fun getDeviceInfo(context: Context): DeviceInfo {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    val brand = Build.BRAND
    val device = Build.DEVICE
    val product = Build.PRODUCT
    val hardware = Build.HARDWARE
    val serial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        try {
            Build.getSerial()
        } catch (e: SecurityException) {
            "Permission required"
        }
    } else {
        Build.SERIAL
    }

    val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val simCountryIso = telephonyManager.simCountryIso
    val networkOperatorName = telephonyManager.networkOperatorName
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiInfo = wifiManager.connectionInfo
    val macAddress = wifiInfo.macAddress

    val displayMetrics = context.resources.displayMetrics
    val widthPixels = displayMetrics.widthPixels
    val heightPixels = displayMetrics.heightPixels
    val densityDpi = displayMetrics.densityDpi

    return DeviceInfo(
        manufacturer = manufacturer,
        model = model,
        brand = brand,
        device = device,
        product = product,
        hardware = hardware,
        serial = serial,
        androidId = androidId,
        simCountryIso = simCountryIso,
        networkOperatorName = networkOperatorName,
        macAddress = macAddress,
        widthPixels = widthPixels,
        heightPixels = heightPixels,
        densityDpi = densityDpi
    )
}
data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val brand: String,
    val device: String,
    val product: String,
    val hardware: String,
    val serial: String,
    val androidId: String,
    val simCountryIso: String,
    val networkOperatorName: String,

    val macAddress: String,
    val widthPixels: Int,
    val heightPixels: Int,
    val densityDpi: Int
)