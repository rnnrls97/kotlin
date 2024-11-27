package com.renanfran.transactionapp.android.feature.stats

import android.graphics.BitmapFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
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
    val imagesState = viewModel.images.collectAsState(initial = emptyList())
    val imageToDelete = remember { mutableStateOf<RandomImageEntity?>(null) }
    val showDialog = remember { mutableStateOf(false) }

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
                items(items = imagesState.value) { imageEntity ->
                    ImageCard(
                        imageEntity = imageEntity,
                        onLongPress = {
                            imageToDelete.value = imageEntity // Update using `.value`
                            showDialog.value = true // Update using `.value`
                        }
                    )
                }
            }
        }
    }

// Confirmation Dialog
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Excluir Imagem") },
            text = { Text("Deseja realmente excluir essa imagem?") },
            confirmButton = {
                TextButton(onClick = {
                    imageToDelete.value?.let { viewModel.deleteImage(it) }
                    showDialog.value = false
                }) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCard(imageEntity: RandomImageEntity, onLongPress: () -> Unit) {
    val bitmap = remember {
        BitmapFactory.decodeByteArray(imageEntity.imageBitmap, 0, imageEntity.imageBitmap.size)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = {}, // No-op for regular clicks
                onLongClick = onLongPress // Trigger on long press
            )
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Saved Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}