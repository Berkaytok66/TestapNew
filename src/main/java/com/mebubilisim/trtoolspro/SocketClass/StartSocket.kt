
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

object SocketServer {
    private var serverSocket: ServerSocket? = null
    private const val TAG = "SocketServer"
    private val clientSockets = mutableListOf<Socket>()
    private var writer: BufferedWriter? = null
    private var reader: BufferedReader? = null
    fun startServer(port: Int) {
       if (serverSocket != null && !serverSocket!!.isClosed) {
            ServerDataRepository.portPostMessage("Sunucu zaten çalışıyor.")
           // Coroutine başlatma
           CoroutineScope(Dispatchers.Default).launch {
               delay(2000) // 2 saniye bekle
               ServerDataRepository.portPostMessage("Komut Bekleniyor...")
           }
           return
       }

        Thread {
            try {
                serverSocket = ServerSocket(port)
                ServerDataRepository.portPostMessage("Sunucu port $port üzerinde dinleniyor...")
                while (true) {
                    val clientSocket = serverSocket!!.accept()
                    clientSockets.add(clientSocket)

                    ServerDataRepository.portPostMessage("İstemci bağlandı")

                    // Coroutine başlatma
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(2000) // 2 saniye bekle
                        ServerDataRepository.portPostMessage("Komut Bekleniyor...")
                    }
                    handleClient(clientSocket)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Coroutine başlatma
                CoroutineScope(Dispatchers.Default).launch {
                    delay(2000) // 2 saniye bekle
                    ServerDataRepository.portPostMessage("Hata: ${e.message}")
                }
            }
        }.start()
    }

    private fun handleClient(clientSocket: Socket) {
        reader = clientSocket.getInputStream().bufferedReader()
        writer = clientSocket.getOutputStream().bufferedWriter()
        Thread {
            try {
               // val secretKey = "!1qaz2WSX3edc%56" // 16-byte key, güvenli bir şekilde saklanmalı ve paylaşılmalı

                while (true) {
                    val message = reader!!.readLine() ?: break

             //       val decryptedText = AESHelper.decrypt(message, secretKey)
                  //  Log.d(TAG, "Alınan mesaj: $message")
                    ServerDataRepository.postMessage(message) // Mesajı paylaşılan depoya gönder


                    // Yanıt gönder


                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "Hata: ${e.message}")
            } finally {
                try {
                     //clientSocket.close()
                     //clientSockets.remove(clientSocket)
                } catch (e: Exception) {
                    Log.e(TAG, "Soket kapatılırken hata oluştu: ${e.message}")
                    e.printStackTrace()
                }
            }
        }.start()
    }

    @Synchronized
    fun sendMessageToAllClients(
        message: String? = null,
        success: Boolean? = null,
        statusReason: String? = null,
        functionName: String? = null,
        progress: Int? = null,

    ) {
        thread {
            try {
                val secretKey = "!1qaz2WSX3edc%56" // 16-byte key, güvenli bir şekilde saklanmalı ve paylaşılmalı
                for (clientSocket in clientSockets) {
                    if (clientSocket.isClosed) {
                        Log.d(TAG, "Client socket is closed, skipping")
                        continue
                    }
                    try {
                         val dataToSend = JSONObject().apply {
                             if (success != null) {
                                 put("status", JSONObject().apply {
                                     put("code", if (success) "success" else "error")
                                     put("reason", statusReason ?: "")
                                 })
                             } else {
                                 put("status", JSONObject().apply {
                                     put("code", "pending")
                                     put("reason", statusReason ?: "Bekleniyor")
                                 })
                             }
                             // Fonksiyon ismini ekle (eğer mevcutsa)
                             functionName?.let {
                                 put("functionName", it)
                             }

                             // Progress bilgisini ekle (eğer mevcutsa)
                             progress?.let {
                                 put("progress", it)
                             }

                             // Mesajı ekle (eğer mevcutsa), yoksa null olarak ekle
                             if (message != null) {
                                 put("message", message)
                             } else {
                                 put("message", JSONObject.NULL)
                             }
                        }.toString()
                        val encryptedData = AESHelper.encrypt(dataToSend, secretKey)// şifreli  json data
                        writer?.write(dataToSend)
                        writer?.newLine()
                        writer?.flush()
                        return@thread

                    } catch (e: Exception) {
                        Log.e(TAG, "Mesaj gönderme sırasında hata oluştu: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Mesaj gönderme sırasında hata oluştu: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    @Synchronized
    fun sendDeviceInfo(
        Manufacturer: String? = null,
        Model: String? = null,
        Brand: String? = null,
        Device: String? = null,
        Product: String? = null,
        Hardware: String? = null,
        Serial: String? = null,
        Android_ID: String? = null,
        SIM_Country_ISO: String? = null,
        Network_Operator: String? = null,
        MAC_Address: String? = null,
        Screen_Width_pixels: String? = null,
        Screen_Height_pixels: String? = null,
        Density_DPI: String? = null
    ) {

        thread {
            try {
                val secretKey = "!1qaz2WSX3edc%56" // 16-byte key, güvenli bir şekilde saklanmalı ve paylaşılmalı
                for (clientSocket in clientSockets) {
                    if (clientSocket.isClosed) {
                        Log.d(TAG, "Client socket is closed, skipping")
                        continue
                    }
                    try {
                        val dataToSend = JSONObject().apply {
                            Manufacturer?.let { put("Manufacturer", it) }
                            Model?.let { put("Model", it) }
                            Brand?.let { put("Brand", it) }
                            Device?.let { put("Device", it) }
                            Product?.let { put("Product", it) }
                            Hardware?.let { put("Hardware", it) }
                            Serial?.let { put("Serial", it) }
                            Android_ID?.let { put("Android_ID", it) }
                            SIM_Country_ISO?.let { put("SIM_Country_ISO", it) }
                            Network_Operator?.let { put("Network_Operator", it) }
                            MAC_Address?.let { put("MAC_Address", it) }
                            Screen_Width_pixels?.let { put("Screen_Width_pixels", it) }
                            Screen_Height_pixels?.let { put("Screen_Height_pixels", it) }
                            Density_DPI?.let { put("Density_DPI", it) }
                        }.toString()

                        val encryptedData = AESHelper.encrypt(dataToSend, secretKey) // şifreli json data
                        writer?.write(encryptedData)
                        writer?.newLine()
                        writer?.flush()

                        return@thread

                    } catch (e: Exception) {
                        Log.e(TAG, "Mesaj gönderme sırasında hata oluştu: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Mesaj gönderme sırasında hata oluştu: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    fun stopServer() {
        try {
            serverSocket?.close()
            clientSockets.forEach { it.close() }
            clientSockets.clear()
            serverSocket = null
            Log.d(TAG, "Sunucu kapatıldı.")
        } catch (e: Exception) {
            Log.e(TAG, "Sunucu kapatılırken hata oluştu: ${e.message}")
            e.printStackTrace()
        }
    }
}
