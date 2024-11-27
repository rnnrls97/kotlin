package com.renanfran.transactionapp.android.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.renanfran.transactionapp.android.data.dao.RandomImageDao
import com.renanfran.transactionapp.android.data.dao.TransactionDao
import com.renanfran.transactionapp.android.data.model.RandomImageEntity
import com.renanfran.transactionapp.android.data.model.TransactionEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Database(entities = [TransactionEntity::class, RandomImageEntity::class], version = 2, exportSchema = false)
abstract class TransactionDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun randomImageDao(): RandomImageDao

    companion object {
        const val DATABASE_NAME = "transaction_database"

        @Volatile
        private var INSTANCE: TransactionDatabase? = null

        fun getInstance(context: Context): TransactionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}