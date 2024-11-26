package com.renanfran.transactionapp.android.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.renanfran.transactionapp.android.data.model.RandomImageEntity
import com.renanfran.transactionapp.android.data.model.TransactionEntity
import com.renanfran.transactionapp.android.data.model.TransactionSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface RandomImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: RandomImageEntity)

    @Query("SELECT * FROM random_images ORDER BY timestamp DESC")
    fun getAllImages(): Flow<List<RandomImageEntity>> // Should return a Flow
}