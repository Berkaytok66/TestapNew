package com.mebubilisim.trtoolspro.CameraFile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.mebubilisim.trtoolspro.CameraFile.ui.theme.TrtoolsproTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : ComponentActivity() {

    companion object {
        private const val TAG = "CameraActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrtoolsproTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CameraView()
                }
            }
        }
    }
}

@Composable
fun CameraView() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val isCameraInitialized = remember { mutableStateOf(false) }
    val camera = remember { mutableStateOf<androidx.camera.core.Camera?>(null) }
    val imageCapture = remember { mutableStateOf(ImageCapture.Builder().build()) }
    val executor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    val capturedImage = remember { mutableStateOf<Bitmap?>(null) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                Log.d("CameraActivity.TAG", "Kamera izni verildi.")
                initializeCameraAsync(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    cameraProviderFuture = cameraProviderFuture,
                    previewView = previewView,
                    camera = camera,
                    imageCapture = imageCapture.value,
                    isCameraInitialized = isCameraInitialized
                )
            } else {
                isCameraInitialized.value = false
                Log.e("CameraActivity.TAG", "Kamera izni reddedildi.")
            }
        }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("CameraActivity.TAG", "Kamera izni zaten verilmiş.")
            initializeCameraAsync(
                context = context,
                lifecycleOwner = lifecycleOwner,
                cameraProviderFuture = cameraProviderFuture,
                previewView = previewView,
                camera = camera,
                imageCapture = imageCapture.value,
                isCameraInitialized = isCameraInitialized
            )
        } else {
            Log.d("CameraActivity.TAG", "Kamera izni isteniyor.")
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isCameraInitialized.value) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    listOf(0.6f, 1f, 2f, 6f, 10f).forEach { zoom ->
                        ZoomButton(text = "${zoom}x") {
                            try {
                                camera.value?.cameraControl?.setZoomRatio(zoom)
                                Log.d("CameraActivity.TAG", "Zoom ayarlandı: $zoom")
                            } catch (e: Exception) {
                                Log.e("CameraActivity.TAG", "Zoom ayarlanamadı: ${e.message}")
                            }
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }

                Button(
                    onClick = {
                        takePhoto(
                            imageCapture.value,
                            executor,
                            context
                        ) { bitmap ->
                            capturedImage.value = bitmap
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Fotoğraf Çek")
                }

                capturedImage.value?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Çekilen Fotoğraf",
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .size(200.dp)
                    )
                }
            }
        } else {
            Text(
                text = "Kamera izni verilmedi veya kamera başlatılamadı.",
                modifier = Modifier.align(Alignment.Center)
            )
            Log.e("CameraActivity.TAG", "Kamera başlatılamadı veya izin verilmedi.")
        }
    }
}

private fun initializeCameraAsync(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    cameraProviderFuture: com.google.common.util.concurrent.ListenableFuture<ProcessCameraProvider>,
    previewView: PreviewView,
    camera: androidx.compose.runtime.MutableState<androidx.camera.core.Camera?>,
    imageCapture: ImageCapture,
    isCameraInitialized: androidx.compose.runtime.MutableState<Boolean>
) {
    cameraProviderFuture.addListener({
        try {
            Log.d("CameraActivity.TAG", "Kamera başlatma işlemi başladı.")
            val cameraProvider = cameraProviderFuture.get()
            Log.d("CameraActivity.TAG", "CameraProvider alındı: $cameraProvider")

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            Log.d("CameraActivity.TAG", "Kamera seçici ayarlandı: $cameraSelector")

            val preview = androidx.camera.core.Preview.Builder().build()
            Log.d("CameraActivity.TAG", "Preview oluşturuldu.")

            preview.setSurfaceProvider(previewView.surfaceProvider)
            Log.d("CameraActivity.TAG", "SurfaceProvider ayarlandı.")

            camera.value = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
            Log.d("CameraActivity.TAG", "Kamera lifecycle'a bağlandı.")

            isCameraInitialized.value = true
            Log.d("CameraActivity.TAG", "Kamera başarıyla başlatıldı.")
        } catch (e: Exception) {
            Log.e("CameraActivity.TAG", "Kamera başlatma sırasında hata: ${e.message}")
            isCameraInitialized.value = false
        }
    }, ContextCompat.getMainExecutor(context))
}

private fun takePhoto(
    imageCapture: ImageCapture,
    executor: ExecutorService,
    context: Context,
    onImageCaptured: (Bitmap) -> Unit
) {
    val outputOptions = ImageCapture.OutputFileOptions.Builder(createTempFile()).build()
    imageCapture.takePicture(outputOptions, executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraActivity.TAG", "Fotoğraf kaydedilemedi: ${exception.message}")
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                // Fotoğraf başarılı bir şekilde kaydedildi, burada görüntüyü işlemeye devam edin.
                val imageProxy: ImageProxy? = outputFileResults.savedUri?.let { uri ->
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)?.let {
                            onImageCaptured(it)
                            null
                        }
                    }
                }
                imageProxy?.close()
            }
        })
}

@Composable
fun ZoomButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(50.dp)
    ) {
        Text(text)
    }
}
