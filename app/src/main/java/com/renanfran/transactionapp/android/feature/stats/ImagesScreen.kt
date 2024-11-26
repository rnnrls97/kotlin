package com.renanfran.transactionapp.android.feature.stats

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.renanfran.transactionapp.android.data.model.RandomImageEntity

@Composable
fun ImagesScreen(navController: NavController, viewModel: ImagesViewModel = hiltViewModel()) {
    val imagesState = viewModel.images.collectAsState() // Ensure this is State<List<RandomImageEntity>>

    Surface(modifier = Modifier.fillMaxSize()) {
        if (imagesState.value.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No images available")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Ensure you are passing a List<RandomImageEntity> here
                items(items = imagesState.value) { imageEntity ->
                    ImageCard(imageEntity) // Your composable for each item
                }
            }
        }
    }
}

@Composable
fun ImageCard(imageEntity: RandomImageEntity) {
    val bitmap = remember { BitmapFactory.decodeByteArray(imageEntity.imageBitmap, 0, imageEntity.imageBitmap.size) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Saved Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}