package com.mebubilisim.trtoolspro.ConfirmationPage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mebubilisim.trtoolspro.ConfirmationPage.ui.theme.TrtoolsproTheme
import com.mebubilisim.trtoolspro.MainActivity
import com.mebubilisim.trtoolspro.R

import com.mebubilisim.trtoolspro.TestPages.ui.DoubleTouchScreenControlActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
class ResultScreenActivity : ComponentActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val isVisible = mutableStateOf(false)
    var stringExtra : String = ""
    private var intentKey: Int = 0
    var showMessageBackgroundColor: Color? = null
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stringExtra = intent.getStringExtra("ShowMesage").toString()
        intentKey = intent.getIntExtra("intentKey", 0)  // Eğer anahtar bulunamazsa 0 döner
        showMessageBackgroundColor = if (stringExtra == "Dokunmatik ekran testini iptal ettiniz.") Color(0x00FA0404) else Color(0xCB09F609)
        setContent {
            TrtoolsproTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    coroutineScope.launch {
                        delay(1000) // 1 saniye beklet
                        isVisible.value = true

                        delay(3000) // 3 saniye daha beklet
                        isVisible.value = false
                    }



                    MultiTouchTestScreen(stringExtra.toString(),ServerDataRepository.messages, this)

            }
        }
    }
}
@ExperimentalAnimationApi
@Composable
fun AnimatedBanner(isVisible: MutableState<Boolean>,title: String,showMessageBackgroundColor: Color) {
    AnimatedVisibility(
        visible = isVisible.value,
        Modifier.background(showMessageBackgroundColor),
        enter = expandVertically(
            // Daha yavaş bir animasyon için spring'in stiffness değerini düşürün
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
        ),
        exit = shrinkVertically(
            // Çıkış animasyonu için de yavaş bir animasyon
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
        )
    )  {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(showMessageBackgroundColor)
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .padding(16.dp, 16.dp, 16.dp, 12.dp)
                        .background(showMessageBackgroundColor)
                )

            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiTouchTestScreen(title: String, messagesFlow: StateFlow<String>, context: Context) {
    val message = messagesFlow.collectAsState().value
    if (message.equals("iptal")){
       // sendCancellationNotice("localhost",8080,"Admin Tarafından iptal edildi")
        val intent = Intent(context, MainActivity::class.java).apply {
       //     sendCancellationNotice("localhost",8080,"Admin Tarafından İptal Edildi")
        }
        context.startActivity(intent)
        (context as? Activity)?.finish()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Cok Noktalı Dokunma", color = Color.White)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White
                ),
            )
        },

        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .background(Color(0xFF14111A))
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                showMessageBackgroundColor?.let { AnimatedBanner(isVisible,title, it) }
                Spacer(modifier = Modifier.height(48.dp)) // Resmin üst kısmını açık bırakmak için Spacer eklenir.
                Image(
                    painter = painterResource(id = R.drawable.one_click_image),
                    contentDescription = "Çoklu Dokunma Testi",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Spacer(modifier = Modifier.height(24.dp)) // Resim ve metin arasında boşluk bırakmak için Spacer eklenir.
                Text(
                    text = "Bu testi, dokunmatik ekranda cihazınızın birden fazla dokunmatik noktayı aynı anda ne kadar doğru algılayıp bunlara yanıt verdiğini kontrol etmek üzere kullanın.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = {
                        when(intentKey){
                            0 -> {
                                var intent = Intent(context, DoubleTouchScreenControlActivity::class.java)
                                context.startActivity(intent)
                                (context as? Activity)?.finish()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .padding(bottom = 16.dp),
                    shape = MaterialTheme.shapes.large.copy(CornerSize(0.dp)), // Köşeleri yok etmek için
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {

                    Text(
                        text = "Başlat",
                        color = Color.White
                    )
                }

            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    TrtoolsproTheme {
       // MultiTouchTestScreen("Demo")

        }
    }
}
