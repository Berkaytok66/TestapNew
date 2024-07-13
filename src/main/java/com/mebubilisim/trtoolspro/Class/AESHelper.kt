
import android.util.Base64
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

// AESHelper adında bir yardımcı sınıf tanımlıyoruz. Bu sınıf, AES şifreleme ve çözme işlemleri için yöntemler içerir.
object AESHelper {
    // AES algoritmasını ve CBC modunu kullanacağımızı belirtiyoruz. PKCS5Padding, şifreleme sırasında veriyi belirli bir uzunluğa doldurmak için kullanılır.
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    // Initialization Vector (IV) belirliyoruz. Bu, şifreleme sırasında kullanılan bir başlangıç değeridir ve genellikle rastgele olmalıdır.
    private val iv = IvParameterSpec("T09MdSGyK2PjzikK".toByteArray()) // 16-byte IV

    // AES için bir anahtar üretir. Bu anahtar 256-bit uzunluğunda olacaktır.
    fun generateKey(): Key {
        val keyGen = KeyGenerator.getInstance(ALGORITHM) // AES algoritması için bir KeyGenerator örneği oluşturur.
        keyGen.init(256) // Anahtar uzunluğunu 256-bit olarak belirler.
        return keyGen.generateKey() // Anahtarı üretir ve döndürür.
    }

    // Veriyi AES algoritması ile şifreler ve şifrelenmiş veriyi Base64 formatında döndürür.
    fun encrypt(data: String, secretKey: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION) // AES/CBC/PKCS5Padding dönüşümünü kullanarak bir Cipher örneği oluşturur.
        val keySpec = SecretKeySpec(secretKey.toByteArray(), ALGORITHM) // Verilen secretKey kullanarak bir SecretKeySpec oluşturur.
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv) // Cipher'ı şifreleme modunda başlatır.
        val encryptedData = cipher.doFinal(data.toByteArray()) // Veriyi şifreler ve şifrelenmiş bayt dizisini alır.
        return Base64.encodeToString(encryptedData, Base64.DEFAULT) // Şifrelenmiş veriyi Base64 formatında döndürür.
    }

    // Şifrelenmiş veriyi AES algoritması ile çözer ve orijinal veriyi döndürür.
    fun decrypt(data: String, secretKey: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION) // AES/CBC/PKCS5Padding dönüşümünü kullanarak bir Cipher örneği oluşturur.
        val keySpec = SecretKeySpec(secretKey.toByteArray(), ALGORITHM) // Verilen secretKey kullanarak bir SecretKeySpec oluşturur.
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv) // Cipher'ı çözme modunda başlatır.
        val decodedData = Base64.decode(data, Base64.DEFAULT) // Base64 formatındaki şifrelenmiş veriyi çözer.
        val decryptedData = cipher.doFinal(decodedData) // Şifrelenmiş bayt dizisini çözer ve orijinal veriyi alır.
        return String(decryptedData) // Orijinal veriyi döndürür.
    }
}
