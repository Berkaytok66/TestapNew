import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ServerDataRepository {
    private val _messages = MutableStateFlow<String>("Komut Bekleniyor...")
    private val portMesagge = MutableStateFlow<String>("Komut Bekleniyor...")
    val messages = _messages.asStateFlow()
    val portMessage = portMesagge.asStateFlow()
    val pageController = MutableStateFlow<Int>(0)

    fun postMessage(newMessage: String) {
        _messages.value = newMessage
        updatePageController()
    }
    fun portPostMessage(newMessage: String){
        portMesagge.value = newMessage
    }
    private fun updatePageController() {
        when (_messages.value) {
            "all" -> {
                pageController.value = 1 //Tüm Testleri Başlat
            }
            "atla" -> {
                pageController.value = 2 // Test Sırasında Atla
            }
            "iptal" -> {
                pageController.value = 3 // Testi iptal Et
            }
            "screntest" -> {
                pageController.value = 4 // Ekran dokunmatik kontrol
            }
            "getCiftDokunmatikKontrol" -> {
                pageController.value = 5 // Cift Tıklama Testi
            }
            "pixelKontrol" -> {
                pageController.value = 6 // Pixel Test
            }
            "brightness" -> {
                pageController.value = 7 // Brightness Testi (Parlaklık)
            }
            "soundTest" -> {
                pageController.value = 8 // (ses)(Mic)(Ahize)(Jack)
            }
            "WifiTest" -> {
                pageController.value = 9 // Wifi test sayfası
            }
            "BluetoothTest" -> {
                pageController.value = 10 // Bluetooth test
            }
            "NfcTest" -> {
                pageController.value = 11 // Nfc test
            }
            "GpsTest" -> {
                pageController.value = 12 // Gps test
            }
            else -> {
                pageController.value = 0
            }
        }
    }

    fun resetMessage() {
        _messages.value = "Komut Bekleniyor..."
    }

    fun resetPageController() {
        pageController.value = 0
    }
}
