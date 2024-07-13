package com.mebubilisim.trtoolspro.SountTest

import ServerDataRepository
import SocketServer
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
<<<<<<< HEAD
=======
import com.mebubilisim.trtoolspro.Class.PageState
>>>>>>> bafdf16 (2 commit)
import com.mebubilisim.trtoolspro.Class.iptalAndNextClass
import com.mebubilisim.trtoolspro.MainActivity
import com.mebubilisim.trtoolspro.R
import com.mebubilisim.trtoolspro.SountTest.ui.theme.TrtoolsproTheme
<<<<<<< HEAD
import com.mebubilisim.trtoolspro.TestPages.ui.DeadPixelScreenTestActivity
=======
>>>>>>> bafdf16 (2 commit)
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class SountTestPage : ComponentActivity() {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private val fileName by lazy { "${externalCacheDir?.absolutePath}/test_audio.3gp" }
    private val TAG = "SountTestPage"
    private var isRecording = false
    private var isPlaying = false

<<<<<<< HEAD
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        queueMessage {
            SocketServer.sendMessageToAllClients(
                functionName = "SountTestPage",
                message = "Testi Ekranda gördüğünüz butona basarak başlatın.",
                progress = 0
            )
        }
=======
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            setupContent()
        } else {
            Log.e(TAG, "Permissions not granted")
            queueMessage {
                SocketServer.sendMessageToAllClients(
                    success = false,
                    functionName = "SountTestPage",
                    message = "The necessary permissions were not given.",
                    progress = 100
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
>>>>>>> bafdf16 (2 commit)
        if (checkPermissions()) {
            setupContent()
        } else {
            requestPermissions()
        }
    }

    private fun setupContent() {
        setContent {
            TrtoolsproTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SpeakerTestScreen(
                        onTestSpeakersAndMic = { testSpeakersAndMic() },
                        onStopTest = { stopTest() },
<<<<<<< HEAD
                        this
=======
                        this@SountTestPage
>>>>>>> bafdf16 (2 commit)
                    )
                }
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return permissions.all { permission ->
            ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
<<<<<<< HEAD
        val requestPermissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.values.all { it }) {
                setupContent()
            } else {
                Log.e(TAG, "Permissions not granted")
                queueMessage {
                    SocketServer.sendMessageToAllClients(
                        success = false,
                        functionName = "SountTestPage",
                        message = "Gerekli izinler verilmedi.",
                        progress = 100
                    )
                }
            }
        }

=======
>>>>>>> bafdf16 (2 commit)
        requestPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    private suspend fun testSpeakersAndMic(): Pair<TestResult, TestResult> = withContext(Dispatchers.IO) {
        queueMessage {
            SocketServer.sendMessageToAllClients(
                functionName = "SountTestPage",
<<<<<<< HEAD
                message = "Test başlatılıyor.",
=======
                message = "Testing is starting.",
>>>>>>> bafdf16 (2 commit)
                progress = 10
            )
        }

        val oldFile = File(fileName)
        if (oldFile.exists()) {
            oldFile.delete()
        }

        var speakerTestResult = TestResult.FAILURE
        var micTestResult = TestResult.FAILURE
        var isSpeakerSuccessful = false
        var isMicSuccessful = false

        try {
            mediaRecorder?.release()
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(fileName)
                prepare()
                start()
                isRecording = true
            }

            // Adım 1: Hoparlörden test sesi çal ve mikrofon ile dinle
            queueMessage {
                SocketServer.sendMessageToAllClients(
                    functionName = "SountTestPage",
<<<<<<< HEAD
                    message = "Hoparlörden test sesi çalınıyor ve mikrofon ile dinleniyor.",
=======
                    message = "The test sound is played through the speaker and listened to through the microphone.",
>>>>>>> bafdf16 (2 commit)
                    progress = 20
                )
            }
            withContext(Dispatchers.Main) {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer.create(this@SountTestPage, R.raw.sound_test).apply {
                    setOnCompletionListener {
                        // Hoparlör testi tamamlandıktan sonra kaydı durdur ve MediaRecorder'ı serbest bırak
                        try {
                            mediaRecorder?.apply {
                                stop()
                                release()
                                queueMessage {
                                    SocketServer.sendMessageToAllClients(
                                        functionName = "SountTestPage",
<<<<<<< HEAD
                                        message = "Hoparlör testi tamamlandı. Mikrofon kaydı durduruluyor.",
=======
                                        message = "Speaker testing completed. Microphone recording is stopped.",
>>>>>>> bafdf16 (2 commit)
                                        progress = 40
                                    )
                                }
                            }
                            mediaRecorder = null
                            isRecording = false
                            speakerTestResult = if (isSpeakerSuccessful) TestResult.SUCCESS else TestResult.FAILURE

                            // Adım 2: Kaydedilen sesi çal ve mikrofonu doğrula
                            queueMessage {
                                SocketServer.sendMessageToAllClients(
                                    functionName = "SountTestPage",
<<<<<<< HEAD
                                    message = "Kaydedilen ses çalınıyor ve mikrofon doğrulanıyor.",
=======
                                    message = "The recorded audio is played and the microphone is verified.",
>>>>>>> bafdf16 (2 commit)
                                    progress = 60
                                )
                            }
                            playBackRecording { success ->
                                micTestResult = if (success) {
                                    queueMessage {
                                        SocketServer.sendMessageToAllClients(
                                            success = true,
                                            functionName = "SountTestPage",
<<<<<<< HEAD
                                            message = "Mikrofon testi başarılı.",
=======
                                            message = "Microphone test successful.",
>>>>>>> bafdf16 (2 commit)
                                            progress = 100
                                        )
                                    }
                                    TestResult.SUCCESS

                                } else {
                                    queueMessage {
                                        SocketServer.sendMessageToAllClients(
                                            success = false,
                                            functionName = "SountTestPage",
<<<<<<< HEAD
                                            message = "Mikrofon testi başarısız.",
=======
                                            message = "Microphone test failed.",
>>>>>>> bafdf16 (2 commit)
                                            progress = 100
                                        )
                                    }
                                    TestResult.FAILURE

                                }
                            }
                        } catch (e: IllegalStateException) {
                            Log.e(TAG, "MediaRecorder durdurulurken hata", e)
                            micTestResult = TestResult.FAILURE
                            queueMessage {
                                SocketServer.sendMessageToAllClients(
                                    success = false,
                                    functionName = "SountTestPage",
<<<<<<< HEAD
                                    message = "MediaRecorder durdurulurken hata.",
=======
                                    message = "Error stopping MediaRecorder.",
>>>>>>> bafdf16 (2 commit)
                                    progress = 100
                                )
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "stopMicrophoneTest sırasında hata", e)
                            micTestResult = TestResult.FAILURE
                            queueMessage {
                                SocketServer.sendMessageToAllClients(
                                    success = false,
                                    functionName = "SountTestPage",
<<<<<<< HEAD
                                    message = "Mikrofon testi sırasında hata.",
=======
                                    message = "Error during microphone test.",
>>>>>>> bafdf16 (2 commit)
                                    progress = 100
                                )
                            }
                        }
                    }
                    start()
                }
            }

            // Amplitüd seviyesi ölçümü
            while (isRecording) {
                val amplitude = mediaRecorder?.maxAmplitude ?: 0
                if (amplitude > 1000) {
                    isSpeakerSuccessful = true
                    Log.i(TAG, "Kayıt sırasında amplitüd seviyesi 1000 dB'i geçti")
                }
                delay(500) // Ölçümler arasında gecikme
            }
        } catch (e: Exception) {
            Log.e(TAG, "Test sesi çalınırken hata", e)
            speakerTestResult = TestResult.FAILURE
            queueMessage {
                SocketServer.sendMessageToAllClients(
                    success = false,
                    functionName = "SountTestPage",
<<<<<<< HEAD
                    message = "Test sesi çalınırken hata.",
=======
                    message = "Error playing test sound.",
>>>>>>> bafdf16 (2 commit)
                    progress = 100
                )
            }
        }

        // Oynatma tamamlanana kadar bekliyoruz
        withContext(Dispatchers.Main) {
            while (isPlaying) {
                delay(100) // Oynatma tamamlanana kadar bekle
            }
        }

        queueMessage {
            SocketServer.sendMessageToAllClients(
                success = speakerTestResult == TestResult.SUCCESS && micTestResult == TestResult.SUCCESS,
                functionName = "SountTestPage",
<<<<<<< HEAD
                message = "Test tamamlandı.",
                progress = 100
            )
        }

=======
                message = "Testing completed.",
                progress = 100
            )
        }
        Log.e(TAG, "Testing completed.")
        PageState(this@SountTestPage, 1, 8, HandsetTestPage::class.java, MainActivity::class.java)
>>>>>>> bafdf16 (2 commit)
        return@withContext Pair(speakerTestResult, micTestResult)
    }

    private fun playBackRecording(onCompletion: (Boolean) -> Unit) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                setOnCompletionListener { player ->
                    this@SountTestPage.isPlaying = false
                    Log.i(TAG, "Hoparlör ve mikrofon testi başarıyla tamamlandı")
                    player.release()
                    onCompletion(true)
                }
                this@SountTestPage.isPlaying = true
                start()
            } catch (e: IOException) {
                Log.e(TAG, "MediaPlayer veri kaynağı ayarlanırken hata", e)
                onCompletion(false)
            } catch (e: IllegalStateException) {
                Log.e(TAG, "MediaPlayer için geçersiz durum", e)
                onCompletion(false)
            }
        }

        // Amplitüd seviyesi ölçümü
        CoroutineScope(Dispatchers.IO).launch {
            while (isPlaying) {
                val amplitude = mediaRecorder?.maxAmplitude ?: 0
                if (amplitude > 1000) {
                    Log.i(TAG, "Oynatma sırasında amplitüd seviyesi 1000 dB'i geçti")
                }
                delay(500) // Ölçümler arasında gecikme
            }
        }
    }

    private fun stopTest() {
        try {
            mediaPlayer?.release()
            mediaPlayer = null
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
            isPlaying = false
        } catch (e: Exception) {
            Log.e(TAG, "MediaPlayer veya MediaRecorder durdurulurken hata", e)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakerTestScreen(
    onTestSpeakersAndMic: suspend () -> Pair<TestResult, TestResult>,
    onStopTest: () -> Unit,
    context: Context
) {
    var speakerTestResult by remember { mutableStateOf(TestResult.NOT_TESTED) }
    var micTestResult by remember { mutableStateOf(TestResult.NOT_TESTED) }
    var isTesting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val pageController by ServerDataRepository.pageController.collectAsState()
<<<<<<< HEAD
    LaunchedEffect(pageController) {
        if (pageController == 2 || pageController == 3) {
            iptalAndNextClass(context, MainActivity::class.java, DeadPixelScreenTestActivity::class.java, "DoubleTouchScreenControlActivity")
        }
    }
=======

    LaunchedEffect(Unit) {
        isTesting = true
        coroutineScope.launch {
            val (speakerResult, micResult) = onTestSpeakersAndMic()
            speakerTestResult = speakerResult
            micTestResult = micResult
            isTesting = false
        }
    }

    LaunchedEffect(pageController) {
        if (pageController == 2 || pageController == 3) {
            iptalAndNextClass(context, MainActivity::class.java, HandsetTestPage::class.java, "DoubleTouchScreenControlActivity")
        }
    }

>>>>>>> bafdf16 (2 commit)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sound Test") },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Bu test, cihazınızın mikrofonları ve hoparlörleri arasında veri aktarımı yaparak bunların işlevselliğini otomatik olarak doğrular.",
                color = Color.Black
            )

            WarningText("Lütfen ses seviyesinin maksimuma ayarlandığından emin olun")
            WarningText("Yüksek ses kaynaklarından uzaklaşın")
            WarningText("Hiçbir kulaklığın bağlı olmadığından emin olun")
            TestResultItem("Hoparlörler", speakerTestResult)
            TestResultItem("Mikrofon", micTestResult)

            Spacer(modifier = Modifier.weight(1f))
            if (isTesting) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        isTesting = true
                        coroutineScope.launch(Dispatchers.Main) {
                            val (speakerResult, micResult) = onTestSpeakersAndMic()
                            speakerTestResult = speakerResult
                            micTestResult = micResult
                            isTesting = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(48.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Testi Başlat")
                }
            }

            Button(
<<<<<<< HEAD
                onClick = onStopTest,
=======
                onClick = {
                    onStopTest()
                    queueMessage {
                        SocketServer.sendMessageToAllClients(
                            success = false,
                            functionName = "SountTestPage",
                            message = "Kullanıcı Tarafından iptal edildi",
                            progress = 100
                        )
                    }
                    PageState(context, 1, 8, HandsetTestPage::class.java, MainActivity::class.java)
                },
>>>>>>> bafdf16 (2 commit)
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(48.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Testi Durdur")
            }
        }
    }
}

@Composable
fun WarningText(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF9800))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = Color(0xFFFF9800)
        )
    }
}

@Composable
fun TestResultItem(testName: String, result: TestResult) {
    val backgroundColor = when (result) {
        TestResult.SUCCESS -> Color.Green
        TestResult.FAILURE -> Color.Red
        TestResult.NOT_TESTED -> Color.Gray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = testName,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        if (result == TestResult.SUCCESS) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
        } else if (result == TestResult.FAILURE) {
            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
        }
    }
}

enum class TestResult {
    SUCCESS, FAILURE, NOT_TESTED
}

private fun queueMessage(message: () -> Unit) {
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    coroutineScope.launch {
        delay(100) // Her mesaj arasında 100ms gecikme
        message()
    }
}
