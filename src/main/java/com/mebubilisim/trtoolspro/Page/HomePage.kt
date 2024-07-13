package com.mebubilisim.trtoolspro.Page

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mebubilisim.trtoolspro.R
import com.mebubilisim.trtoolspro.ui.theme.TrtoolsproTheme

data class Feature(
    val title: String,
    val description: String,
    val imageResId: Int // Yeni eklenen drawable resim ID'si
)

// Örnek veri listesi
val featureList = listOf(
    Feature("Ekran Testi", "Ekran testi menüsü ile cihazınızın ekranını kontrol edebilir ve calışmayan bölgeleri tespit edebilirsiniz", R.drawable.screen_image),
    Feature("Ekran Testi", "Ekran testi menüsü ile cihazınızın ekranını kontrol edebilir ve calışmayan bölgeleri tespit edebilirsiniz", R.drawable.screen_image),
    Feature("Ekran Testi", "Ekran testi menüsü ile cihazınızın ekranını kontrol edebilir ve calışmayan bölgeleri tespit edebilirsiniz", R.drawable.screen_image),

    // Daha fazla özellik eklenebilir.
)
@Composable
fun HomePage(onFeatureClick: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ClubsLazyRow()
        }

        itemsIndexed(featureList) { index, feature ->
            FeatureCard(feature = feature, index = index, onCardClick = onFeatureClick)
        }
    }
}

@Composable
fun FeatureCard(feature: Feature, index: Int, onCardClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable { onCardClick(index) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = feature.imageResId),
                contentDescription = feature.title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = feature.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = feature.description,
                    fontSize = 16.sp
                )
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TrtoolsproTheme {
        // UserCard()
        HomePage(onFeatureClick = { index ->
            // Gerçek bir uygulamada burada tıklanan özellik için bir aksiyon yapılabilir.
            // Önizleme için bu lambda boş bırakılabilir veya basit bir log işlemi yapabilir.
            println("Feature at index $index clicked")
        })
    }
}
@Composable
fun ClubsLazyRow() {
    // Ana kartı tutacak LazyRow
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(2) { index -> // Dinamik eleman sayısı için 'items' kullandım
            ClubCard(index = index) {
                // Tıklama işlevi burada ele aldım
                when (index){
                    0 -> {
                        Log.d("aaaaa", "index $index")
                    }
                    1 -> {
                        Log.d("aaaaa", "index $index")
                    }

                }

            }
        }

    }
}
@Composable
fun ClubCard(index: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable { onClick() } // Tıklama işlevi ekleniyor
            .size(width = 280.dp, height = 300.dp)
            .background(Color.White),
        shape = RoundedCornerShape(topStart = 16.dp, bottomStart =16.dp, topEnd = 16.dp, bottomEnd = 66.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(modifier = Modifier
            .padding(top = 40.dp , start = 16.dp)
        ) {
            Text(
                if (index % 2 == 0) "Tüm Testleri Başlat" else "Test Klavuzunu Aç",
                fontSize = 20.sp,
                color = Color.Black
            )
            Text(
                if (index % 2 == 0) "herşeyi Test Etmek istiyorsan" else "Nasıl Yapıldığını Öğrenmek İstiyorsan",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                // Buraya profil resmi veya başka bir görsel ekleyebilirsiniz
                // Image(...)


            }

        }
    }
}


