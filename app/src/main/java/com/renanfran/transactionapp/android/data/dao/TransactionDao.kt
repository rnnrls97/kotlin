package com.renanfran.transactionapp.android.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.renanfran.transactionapp.android.data.model.TransactionEntity
import com.renanfran.transactionapp.android.data.model.TransactionSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {


    @Query("SELECT * FROM transactions")
    fun getAllExpense(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = 'Expense' ORDER BY amount DESC LIMIT 5")
    fun getTopExpenses(): Flow<List<TransactionEntity>>


    @Query("SELECT type, date, SUM(amount) AS total_amount FROM transactions where type = :type GROUP BY type, date ORDER BY date")
    fun getAllExpenseByDate(type: String = "Expense"): Flow<List<TransactionSummary>>

    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: Int): TransactionEntity?

    @Insert
    suspend fun insertExpense(TransactionEntity: TransactionEntity)

    @Delete
    suspend fun deleteExpense(TransactionEntity: TransactionEntity)

    @Update
    suspend fun updateExpense(TransactionEntity: TransactionEntity)
}