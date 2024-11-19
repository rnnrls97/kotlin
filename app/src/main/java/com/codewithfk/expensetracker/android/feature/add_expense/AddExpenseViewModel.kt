package com.codewithfk.expensetracker.android.feature.add_expense

import androidx.lifecycle.viewModelScope
import com.codewithfk.expensetracker.android.base.AddExpenseNavigationEvent
import com.codewithfk.expensetracker.android.base.BaseViewModel
import com.codewithfk.expensetracker.android.base.NavigationEvent
import com.codewithfk.expensetracker.android.base.UiEvent
import com.codewithfk.expensetracker.android.data.dao.ExpenseDao
import com.codewithfk.expensetracker.android.data.model.ExpenseEntity
import com.codewithfk.expensetracker.android.data.model.ExpenseLogEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(val dao: ExpenseDao) : BaseViewModel() {
    private val _transactionData = MutableStateFlow<ExpenseEntity?>(null)
    val transactionData: StateFlow<ExpenseEntity?> = _transactionData.asStateFlow()

    suspend fun addExpense(expenseEntity: ExpenseEntity): Boolean {
        return try {
            val expenseId = dao.insertExpense(expenseEntity).toInt()
            if (expenseId > 0) {
                val log = ExpenseLogEntity(
                    id = expenseId,
                    title = expenseEntity.title,
                    amount = expenseEntity.amount,
                    date = expenseEntity.date,
                    type = expenseEntity.type,
                    action = "INSERT",
                    timestamp = System.currentTimeMillis()
                )
                dao.insertLog(log)
            }

            true
        } catch (ex: Throwable) {
            false
        }
    }

    suspend fun saveExpense(expenseEntity: ExpenseEntity): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                if (expenseEntity.id == null) {
                    val expenseId = dao.insertExpense(expenseEntity)
                    if (expenseId > 0) {
                        val log = ExpenseLogEntity(
                            id = expenseId,
                            title = expenseEntity.title,
                            amount = expenseEntity.amount,
                            date = expenseEntity.date,
                            type = expenseEntity.type,
                            action = "INSERT",
                            timestamp = System.currentTimeMillis()
                        )
                        dao.insertLog(log)
                    }
                } else {
                    dao.updateExpense(expenseEntity)

                    val log = ExpenseLogEntity(
                        id = expenseEntity.id,
                        title = expenseEntity.title,
                        amount = expenseEntity.amount,
                        date = expenseEntity.date,
                        type = expenseEntity.type,
                        action = "UPDATE",
                        timestamp = System.currentTimeMillis()
                    )
                    dao.insertLog(log)
                }
            }
            true
        } catch (ex: Throwable) {
            false
        }
    }

    override fun onEvent(event: UiEvent) {
        when (event) {
            is AddExpenseUiEvent.OnAddExpenseClicked -> {
                viewModelScope.launch {
                    val result = saveExpense(event.expenseEntity)
                    if (result) {
                        _navigationEvent.emit(NavigationEvent.NavigateBack)
                    }
                }
            }
            is AddExpenseUiEvent.OnBackPressed -> {
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationEvent.NavigateBack)
                }
            }
            is AddExpenseUiEvent.OnMenuClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(AddExpenseNavigationEvent.MenuOpenedClicked)
                }
            }
        }
    }

    fun loadTransaction(transactionId: Int) {
        viewModelScope.launch {
            val transaction = withContext(Dispatchers.IO) {
                dao.getTransactionById(transactionId)
            }
            _transactionData.value = transaction
        }
    }
    
}

sealed class AddExpenseUiEvent : UiEvent() {
    data class OnAddExpenseClicked(val expenseEntity: ExpenseEntity) : AddExpenseUiEvent()
    object OnBackPressed : AddExpenseUiEvent()
    object OnMenuClicked : AddExpenseUiEvent()
}


