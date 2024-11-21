package com.renanfran.transactionapp.android.feature.add_transaction

import androidx.lifecycle.viewModelScope
import com.renanfran.transactionapp.android.base.AddTransactionNavigationEvent
import com.renanfran.transactionapp.android.base.BaseViewModel
import com.renanfran.transactionapp.android.base.NavigationEvent
import com.renanfran.transactionapp.android.base.UiEvent
import com.renanfran.transactionapp.android.data.dao.TransactionDao
import com.renanfran.transactionapp.android.data.model.TransactionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(val dao: TransactionDao) : BaseViewModel() {
    private val _transactionData = MutableStateFlow<TransactionEntity?>(null)
    val transactionData: StateFlow<TransactionEntity?> = _transactionData.asStateFlow()

    suspend fun AddTransaction(TransactionEntity: TransactionEntity): Boolean {
        return try {
            dao.insertExpense(TransactionEntity)
            true
        } catch (ex: Throwable) {
            false
        }
    }

    suspend fun saveExpense(TransactionEntity: TransactionEntity): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                if (TransactionEntity.id == null) {
                    dao.insertExpense(TransactionEntity) // Insert new transaction
                } else {
                    dao.updateExpense(TransactionEntity) // Update existing transaction
                }
            }
            true
        } catch (ex: Throwable) {
            false
        }
    }

    override fun onEvent(event: UiEvent) {
        when (event) {
            is AddTransactionUiEvent.OnAddTransactionClicked -> {
                viewModelScope.launch {
                    val result = saveExpense(event.TransactionEntity)
                    if (result) {
                        _navigationEvent.emit(NavigationEvent.NavigateBack)
                    }
                }
            }
            is AddTransactionUiEvent.OnBackPressed -> {
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationEvent.NavigateBack)
                }
            }
            is AddTransactionUiEvent.OnMenuClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(AddTransactionNavigationEvent.MenuOpenedClicked)
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

sealed class AddTransactionUiEvent : UiEvent() {
    data class OnAddTransactionClicked(val TransactionEntity: TransactionEntity) : AddTransactionUiEvent()
    object OnBackPressed : AddTransactionUiEvent()
    object OnMenuClicked : AddTransactionUiEvent()
}


