package com.mebubilisim.trtoolspro.EquipmentPage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mebubilisim.trtoolspro.EquipmentPage.ui.theme.TrtoolsproTheme

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
                Log.d("KeyEventTestActivity", "Volume Up Pressed")
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                volumeDownPressed = true
                Log.d("KeyEventTestActivity", "Volume Down Pressed")
            }
            KeyEvent.KEYCODE_POWER -> {
                powerButtonPressed = true
                Log.d("KeyEventTestActivity", "Power Button Pressed")
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun KeyEventTestScreen() {
        Scaffold(
            topBar = {
                
                TopAppBar(
                    title = { Text(text = "Tuş Testi") }
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
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
}
