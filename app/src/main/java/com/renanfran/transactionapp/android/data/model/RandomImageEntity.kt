package com.renanfran.transactionapp.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "random_images")
data class RandomImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageBitmap: ByteArray, // Store the image as a ByteArray
    val timestamp: Long = System.currentTimeMillis() // Optional: Add a timestamp
)