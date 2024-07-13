package com.mebubilisim.trtoolspro.EquipmentPage

<<<<<<< HEAD
=======
import android.annotation.SuppressLint
>>>>>>> bafdf16 (2 commit)
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
<<<<<<< HEAD
=======
import androidx.compose.foundation.Image
>>>>>>> bafdf16 (2 commit)
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
<<<<<<< HEAD
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
=======
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
>>>>>>> bafdf16 (2 commit)
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
<<<<<<< HEAD
=======
import androidx.compose.material3.TopAppBarDefaults
>>>>>>> bafdf16 (2 commit)
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
<<<<<<< HEAD
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mebubilisim.trtoolspro.EquipmentPage.ui.theme.TrtoolsproTheme
=======
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mebubilisim.trtoolspro.Class.navigateAndFinishCurrent
import com.mebubilisim.trtoolspro.EquipmentPage.ui.theme.TrtoolsproTheme
import com.mebubilisim.trtoolspro.MainActivity
import com.mebubilisim.trtoolspro.R
>>>>>>> bafdf16 (2 commit)

class KeyEventTestActivity : ComponentActivity() {

    private var volumeUpPressed by mutableStateOf(false)
    private var volumeDownPressed by mutableStateOf(false)
    private var powerButtonPressed by mutableStateOf(false)

    private val screenOffReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                powerButtonPressed = true
            }
        }
    }

<<<<<<< HEAD
=======
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
>>>>>>> bafdf16 (2 commit)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            volumeUpPressed = savedInstanceState.getBoolean("volumeUpPressed", false)
            volumeDownPressed = savedInstanceState.getBoolean("volumeDownPressed", false)
            powerButtonPressed = savedInstanceState.getBoolean("powerButtonPressed", false)
        }

        setContent {
            TrtoolsproTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KeyEventTestScreen()
                }
            }
        }

        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenOffReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenOffReceiver)
    }
<<<<<<< HEAD

=======
    override fun onResume() {
        super.onResume()
        // Uygulama ön plana geldiğinde yapılacak işlemler
        controllerClick()

    }
>>>>>>> bafdf16 (2 commit)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("volumeUpPressed", volumeUpPressed)
        outState.putBoolean("volumeDownPressed", volumeDownPressed)
        outState.putBoolean("powerButtonPressed", powerButtonPressed)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                volumeUpPressed = true
<<<<<<< HEAD
=======
                controllerClick()
>>>>>>> bafdf16 (2 commit)
                Log.d("KeyEventTestActivity", "Volume Up Pressed")
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                volumeDownPressed = true
<<<<<<< HEAD
=======
                controllerClick()
>>>>>>> bafdf16 (2 commit)
                Log.d("KeyEventTestActivity", "Volume Down Pressed")
            }
            KeyEvent.KEYCODE_POWER -> {
                powerButtonPressed = true
<<<<<<< HEAD
=======
                controllerClick()
>>>>>>> bafdf16 (2 commit)
                Log.d("KeyEventTestActivity", "Power Button Pressed")
            }
        }
        return super.onKeyDown(keyCode, event)
    }
<<<<<<< HEAD

=======
    fun controllerClick(){
        if (volumeUpPressed && volumeDownPressed && powerButtonPressed){
            volumeUpPressed = false
            volumeDownPressed = false
            powerButtonPressed = false
            navigateAndFinishCurrent(this@KeyEventTestActivity,MainActivity::class.java)
        }
    }
>>>>>>> bafdf16 (2 commit)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun KeyEventTestScreen() {
        Scaffold(
            topBar = {
<<<<<<< HEAD

                TopAppBar(
                    title = { Text(text = "Tuş Testi") }
=======

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
>>>>>>> bafdf16 (2 commit)
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
<<<<<<< HEAD
                        .padding(paddingValues)
                        .padding(16.dp),
=======
                        .padding(paddingValues),
>>>>>>> bafdf16 (2 commit)
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
<<<<<<< HEAD
=======
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .background(Color(0xFF4B5563))
                    ) {
                        com.mebubilisim.trtoolspro.SountTest.InfoText(text = "Testi başlat buttonuna basarak testi başlatabilirsiniz.")
                        com.mebubilisim.trtoolspro.SountTest.InfoText(text = "Sessiz bir ortamda olun.")
                        com.mebubilisim.trtoolspro.SountTest.InfoText(text = "Test sırasında ahizeyi kulağınıza yaklaştırın.")
                    }
                    Column(
>>>>>>> bafdf16 (2 commit)
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .background(Color.White)
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                KeyEventItem("Ses (+)", volumeUpPressed)
                                KeyEventItem("Ses (-)", volumeDownPressed)
                                KeyEventItem("Power (On-Of)", powerButtonPressed)
<<<<<<< HEAD
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            volumeUpPressed = false
                            volumeDownPressed = false
                            powerButtonPressed = false

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(text = "Clear Data")
                    }
                }
            }
=======
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Gray)
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.volume_click_image),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(16.dp)
                                    ) {

                                    }
                                }
                            }

                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                }
            }

>>>>>>> bafdf16 (2 commit)
        )
    }

    @Composable
    fun KeyEventItem(text: String, isPressed: Boolean) {
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
                if (isPressed) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Completed",
                        tint = Color.Green,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Gray
                    )
                }
            }
        }
    }
<<<<<<< HEAD
=======
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
>>>>>>> bafdf16 (2 commit)
}
