package com.mebubilisim.trtoolspro.SountTestPages

import ServerDataRepository
import SocketServer
import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ehsanmsz.mszprogressindicator.progressindicator.BallClipRotateMultipleProgressIndicator
import com.ehsanmsz.mszprogressindicator.progressindicator.LineScalePulseOutRapidProgressIndicator
import com.mebubilisim.trtoolspro.Class.PageState
import com.mebubilisim.trtoolspro.Class.iptalAndNextClass
import com.mebubilisim.trtoolspro.MainActivity
import com.mebubilisim.trtoolspro.R
import com.mebubilisim.trtoolspro.SountTestPages.ui.theme.TrtoolsproTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundTestPageActivity : ComponentActivity() {
    private lateinit var viewModel: SoundTestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SoundTestViewModel::class.java)

        setContent {
            TrtoolsproTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScaffoldWithTopBar(viewModel)
                    viewModel.startDetection()
                }
            }
            val pageController by ServerDataRepository.pageController.collectAsState()
            LaunchedEffect(pageController) {
                if (pageController == 2 || pageController == 3) {
                    viewModel.stopDetection()
                    viewModel.releaseResources()
                    delay(500) // Bu bekleme süresi kaynakların serbest bırakılması için
                    iptalAndNextClass(
                        this@SoundTestPageActivity,
                        MainActivity::class.java,
                        MainActivity::class.java,
                        "SoundTestPageActivity"
                    )
                    finish() // Activity'i kapat
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopDetection() // Ses oynatmayı durdur
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releaseResources() // Tüm kaynakları serbest bırak
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ScaffoldWithTopBar(viewModel: SoundTestViewModel) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Ses Testi")
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                    ),
                )
            }, content = {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                        .background(Color(0xE2FFFFFF)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SpeakerTestContent(viewModel)
                }
            })
    }

    @Composable
    fun SpeakerTestContent(viewModel: SoundTestViewModel) {
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
            SpeakerTestWarnings()
            Spacer(modifier = Modifier.height(20.dp))
            TestResultIndicator(viewModel)
            TutorialModalBottomSheet(viewModel.ahizeTesting, viewModel) // Ahize Testinde acılan dialog
            if (viewModel.jackTestStart.value) {
                JackKontrolDialog(viewModel.jackTestStart, viewModel, this@SoundTestPageActivity)
            }
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun TestResultIndicator(viewModel: SoundTestViewModel) {
        val coroutineScope = rememberCoroutineScope()
        Card(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                RepeatableTestItem(text = "Hoparlör Testi", testState = viewModel.micAndKontroler.value)
                Divider()
                RepeatableTestItem(text = "Mikrofon Testi", testState = viewModel.micAndKontroler.value)
                Divider()
                RepeatableTestItem(text = "Ahize Testi", testState = viewModel.ahizeControllers.value)
                Divider()
                RepeatableTestItem(text = "Jack Testi", testState = viewModel.jackController.value)
            }
            if (viewModel.allTestsCompleted.value) {
                coroutineScope.launch {
                    viewModel.stopDetection()
                    viewModel.releaseResources()
                    PageState(
                        this@SoundTestPageActivity, 1, 8,
                        MainActivity::class.java,
                        MainActivity::class.java
                    )
                    finish()
                }
            }
            if (viewModel.jackController.value ==1){
                PageState(
                    this@SoundTestPageActivity, 1, 8,
                    MainActivity::class.java,
                    MainActivity::class.java
                )
            }
        }
    }

    @Composable
    fun MyCustomProgressIndicator() {
        BallClipRotateMultipleProgressIndicator()
    }

    @Composable
    fun RepeatableTestItem(text: String, testState: Int) {
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
                        MyCustomProgressIndicator()
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TutorialModalBottomSheet(showModalBottomSheet: MutableState<Boolean>, viewModel: SoundTestViewModel) {
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
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Image
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                LineScalePulseOutRapidProgressIndicator()
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth() // Tüm genişliği kapla
                            .padding(horizontal = 16.dp), // Yatayda 16 dp padding
                        horizontalAlignment = Alignment.CenterHorizontally // İçerikleri yatayda ortala
                    ) {
                        Text(
                            "Ahizeden açılan anonsu duydunuz mu? Duyduysanız yeşil butona, duymadıysanız kırmızı butona basarak devam edin.",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 14.sp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                viewModel.ahizeControllers.value = 1
                                scope.launch {
                                    showModalBottomSheet.value = false
                                }
                                viewModel.startListeningHeadphoneState()
                                SocketServer.sendMessageToAllClients(
                                    success = true,
                                    statusReason = "Handset Working",
                                    functionName = "playSoundThroughEarPiece",
                                    progress = 100
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                            modifier = Modifier
                                .size(70.dp)
                                .padding(top = 16.dp),
                            shape = RectangleShape
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.ahizeControllers.value = 2
                                scope.launch {
                                    SocketServer.sendMessageToAllClients(
                                        success = false,
                                        statusReason = "Handset Not Working",
                                        functionName = "playSoundThroughEarPiece",
                                        progress = 100
                                    )
                                    delay(2000)
                                    viewModel.startListeningHeadphoneState()
                                    showModalBottomSheet.value = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier
                                .size(70.dp)
                                .padding(top = 16.dp),
                            shape = RectangleShape
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Failed",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
            }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun JackKontrolDialog(showModalBottomSheet: MutableState<Boolean>, viewModel: SoundTestViewModel, context: Context) {
        val scope = rememberCoroutineScope()
        var skipPartially by remember { mutableStateOf(true) }
        viewModel.startListeningHeadphoneState()
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
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (viewModel.jackController.value == 1) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Completed",
                                        tint = Color.Green,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    showModalBottomSheet.value = false
                                } else {
                                    LineScalePulseOutRapidProgressIndicator()
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Cihazınızın kulaklık jak girişini test edebilmemiz için lütfen bir jak takın.",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 14.sp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    viewModel.stopDetection()
                                    viewModel.releaseResources()
                                    delay(500) // Kaynakların serbest bırakılması için bekleme süresi
                                    showModalBottomSheet.value = false
                                    SocketServer.sendMessageToAllClients(success = false, statusReason = "It was stated that the jack input was damaged", message = "All Test Finish", functionName = "startListeningHeadphoneState", progress = 100)
                                    PageState(context, 1, 8, MainActivity::class.java, MainActivity::class.java)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier
                                .padding(top = 16.dp),
                            shape = RectangleShape
                        ) {
                            Text(text = "Hata")
                        }
                        Button(
                            onClick = {
                                scope.launch {
                                    viewModel.stopDetection()
                                    viewModel.releaseResources()
                                    delay(500) // Kaynakların serbest bırakılması için bekleme süresi
                                    showModalBottomSheet.value = false
                                    SocketServer.sendMessageToAllClients(success = false, statusReason = "jack test skipped", message = "All Test Finish", functionName = "startListeningHeadphoneState", progress = 100)
                                    PageState(context, 1, 8, MainActivity::class.java, MainActivity::class.java)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                            modifier = Modifier
                                .padding(top = 16.dp),
                            shape = RectangleShape
                        ) {
                            Text(text = "Atla")
                        }
                    }
                }
            }
    }

    @Composable
    fun SpeakerTestWarnings() {
        val warnings = listOf(
            "Lütfen ses seviyesinin maksimuma ayarlandığından emin olun",
            "Yüksek ses kaynaklarından uzaklaşın",
            "Hiçbir kulaklığın bağlı olmadığından emin olun"
        )
        warnings.forEach { warning ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(Color.White)
            ) {
                Text(
                    warning,
                    color = Color.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(all = 8.dp)
                )
            }
        }
    }
}

class SoundTestViewModel(application: Application) : AndroidViewModel(application) {
    private var mediaPlayer: MediaPlayer? = null
    private var audioRecord: AudioRecord? = null
    private var isDetecting = false
    var micAndHoperlorTest = mutableStateOf(false)
    var micAndKontroler = mutableStateOf(0)
    var ahizeTesting = mutableStateOf(false)
    var ahizeControllers = mutableStateOf(0)
    private var headphoneReceiver: BroadcastReceiver? = null
    var jackController = mutableStateOf(0)
    var jackTestStart = mutableStateOf(false)
    var allTestsCompleted = mutableStateOf(false)
    var overallProgress = mutableStateOf(0)
    private val messageQueue = mutableListOf<() -> Unit>()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        initializeMediaPlayer()
        initializeAudioRecord()
        processMessageQueue()
    }

    private fun initializeMediaPlayer() {
        queueMessage {
            SocketServer.sendMessageToAllClients(statusReason = "Hoperlor Test Started", functionName = "initializeMediaPlayer", progress = 10)
        }
        mediaPlayer = MediaPlayer.create(getApplication(), R.raw.voice).apply {
            setOnCompletionListener {
                stopDetection()
                Handler(Looper.getMainLooper()).postDelayed({
                    playSoundThroughEarPiece(getApplication(), R.raw.ahizeanonssount)
                    queueMessage {
                        SocketServer.sendMessageToAllClients(statusReason = "Playing audio from hoperlor", functionName = "initializeMediaPlayer", progress = 50)
                    }
                }, 2000)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeAudioRecord() {
        val sampleRate = 44100
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize)
        queueMessage {
            SocketServer.sendMessageToAllClients(statusReason = "Voice Recording started", functionName = "initializeAudioRecord", progress = 0)
        }
    }

    fun startDetection() {
        if (audioRecord == null || mediaPlayer == null) return
        audioRecord?.startRecording()
        mediaPlayer?.start()
        isDetecting = true
        detectSound()
    }

    private fun detectSound() {
        viewModelScope.launch(Dispatchers.Default) {
            val buffer = ShortArray(1024)
            var read: Int
            var highestAmplitude = 0

            while (isDetecting) {
                read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    val currentAmplitude = buffer.maxOrNull() ?: 0
                    if (currentAmplitude > highestAmplitude) {
                        highestAmplitude = currentAmplitude.toInt()
                    }
                }
            }
            if (highestAmplitude > 20000) {
                micAndHoperlorTest.value = true
                micAndKontroler.value = 1
                updateProgressAndSendResults(success = true, testName = "Sound Test", progress = 25)
            } else {
                micAndHoperlorTest.value = true
                micAndKontroler.value = 2
                updateProgressAndSendResults(success = false, testName = "Sound Test", progress = 25)
            }
        }
    }

    fun stopDetection() {
        mediaPlayer?.pause()
        audioRecord?.stop()
        isDetecting = false
    }

    fun releaseResources() {
        mediaPlayer?.release()
        audioRecord?.release()
        mediaPlayer = null
        audioRecord = null
        stopListeningHeadphoneState()
    }

    override fun onCleared() {
        super.onCleared()
        releaseResources()
    }

    fun playSoundThroughEarPiece(context: Context, resourceId: Int) {
        mediaPlayer?.release() // Mevcut medya oynatıcıyı serbest bırak
        mediaPlayer = MediaPlayer.create(context, resourceId).apply {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            setOnCompletionListener {
                Handler(Looper.getMainLooper()).postDelayed({
                    audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
                    queueMessage {
                        SocketServer.sendMessageToAllClients(statusReason = "change the sound mode from normal to phone mode", functionName = "playSoundThroughEarPiece", progress = 50)
                    }
                    val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .build()

                    setAudioAttributes(audioAttributes)
                    start()

                    setOnCompletionListener {
                        it.release()
                        audioManager.mode = AudioManager.MODE_NORMAL
                    }
                    queueMessage {
                        SocketServer.sendMessageToAllClients(statusReason = "Make the mode normal again", functionName = "playSoundThroughEarPiece", progress = 75)
                    }
                }, 2000)
                ahizeTesting.value = true
            }
            start()
        }
    }

    fun startListeningHeadphoneState() {
        jackTestStart.value = true
        if (headphoneReceiver == null) {
            headphoneReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    intent?.let {
                        if (intent.action == Intent.ACTION_HEADSET_PLUG) {
                            val state = intent.getIntExtra("state", -1)
                            when (state) {
                                0 -> onHeadphonesUnplugged()
                                1 -> {
                                    context?.let { onHeadphonesPlugged() }
                                    stopListeningHeadphoneState()
                                }
                            }
                        }
                    }
                }
            }

            val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
            getApplication<Application>().registerReceiver(headphoneReceiver, filter)
        }
    }

    fun stopListeningHeadphoneState() {
        headphoneReceiver?.let {
            getApplication<Application>().unregisterReceiver(it)
            headphoneReceiver = null
        }
    }

    private fun onHeadphonesPlugged() {
        queueMessage {
            SocketServer.sendMessageToAllClients(success = true, statusReason = "Headphones plugged in", functionName = "startListeningHeadphoneState", progress = 100)
        }
        jackController.value = 1
        updateProgressAndSendResults(success = true, testName = "Jack Test", progress = 100)
    }

    private fun onHeadphonesUnplugged() {
        updateProgressAndSendResults(success = false, testName = "Jack Test", progress = 100)
    }

    private fun updateProgressAndSendResults(success: Boolean, testName: String, progress: Int) {
        overallProgress.value += progress / 4
        queueMessage {
            SocketServer.sendMessageToAllClients(
                success = success,
                statusReason = "$testName completed",
                functionName = "SoundTestViewModel",
                progress = overallProgress.value
            )
        }

        if (overallProgress.value == 100) {
            allTestsCompleted.value = true
            queueMessage {
                SocketServer.sendMessageToAllClients(
                    success = true,
                    statusReason = "All tests completed",
                    functionName = "SoundTestViewModel",
                    progress = 100
                )
            }
        }
    }

    private fun queueMessage(message: () -> Unit) {
        messageQueue.add(message)
    }

    private fun processMessageQueue() {
        scope.launch {
            while (true) {
                if (messageQueue.isNotEmpty()) {
                    messageQueue.removeAt(0).invoke()
                    delay(500) // Her mesaj arasında 500ms gecikme
                } else {
                    delay(100)
                }
            }
        }
    }
}
