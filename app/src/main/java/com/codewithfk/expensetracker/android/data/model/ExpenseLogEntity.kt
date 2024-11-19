package com.codewithfk.expensetracker.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_logs_table")
data class ExpenseLogEntity(
    @PrimaryKey(autoGenerate = true) val logId: Int? = null,
    val id: Int?,
    val title: String,
    val amount: Double,
    val date: String,
    val type: String,
    val action: String,
    val timestamp: Long