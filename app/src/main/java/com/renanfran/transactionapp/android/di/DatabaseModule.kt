package com.renanfran.transactionapp.android.di

import android.content.Context
import com.renanfran.transactionapp.android.data.TransactionDatabase
import com.renanfran.transactionapp.android.data.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.internal.Provider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context, ): TransactionDatabase {
        return TransactionDatabase.getInstance(context)
    }

    @Provides
    fun provideTransactionDao(database: TransactionDatabase): TransactionDao {
        return database.TransactionDao()
    }
}
