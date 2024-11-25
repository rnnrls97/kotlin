package com.renanfran.transactionapp.android.feature.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.viewModelScope
import com.renanfran.transactionapp.android.base.BaseViewModel
import com.renanfran.transactionapp.android.base.HomeNavigationEvent
import com.renanfran.transactionapp.android.base.UiEvent
import com.renanfran.transactionapp.android.utils.Utils
import com.renanfran.transactionapp.android.data.dao.TransactionDao
import com.renanfran.transactionapp.android.data.model.TransactionEntity
import com.renanfran.transactionapp.android.data.service.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val dao: TransactionDao) : BaseViewModel() {
    val expenses = dao.getAllExpense()
    private val _randomImageUrl = MutableStateFlow<Bitmap?>(null)
    val randomImageUrl: StateFlow<Bitmap?> get() = _randomImageUrl

    init {
        fetchRandomImage()
    }

    override fun onEvent(event: UiEvent) {
        when (event) {
            is HomeUiEvent.OnAddTransactionClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(HomeNavigationEvent.NavigateToAddTransaction)
                }
            }

            is HomeUiEvent.OnAddIncomeClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(HomeNavigationEvent.NavigateToAddIncome)
                }
            }

            is HomeUiEvent.OnSeeAllClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(HomeNavigationEvent.NavigateToSeeAll)
                }
            }
        }
    }

    fun getBalance(list: List<TransactionEntity>): String {
        var balance = 0.0
        for (expense in list) {
            if (expense.type == "Receita") {
                balance += expense.amount
            } else {
                balance -= expense.amount
            }
        }
        return Utils.formatCurrency(balance)
    }

    fun getTotalExpense(list: List<TransactionEntity>): String {
        var total = 0.0
        for (expense in list) {
            if (expense.type != "Receita") {
                total += expense.amount
            }
        }

        return Utils.formatCurrency(total)
    }

    fun getTotalIncome(list: List<TransactionEntity>): String {
        var totalIncome = 0.0
        for (expense in list) {
            if (expense.type == "Receita") {
                totalIncome += expense.amount
            }
        }
        return Utils.formatCurrency(totalIncome)
    }

    // New function to delete a transaction
    fun deleteTransaction(expense: TransactionEntity) {
        viewModelScope.launch {
            dao.deleteExpense(expense)
        }
    }

    fun fetchRandomImage() {
        viewModelScope.launch {
            try {
                val random = System.currentTimeMillis()
                val responseBody = RetrofitInstance.api.getRandomImage(random)
                val imageBytes = responseBody.bytes() // Convert to byte array
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                _randomImageUrl.value = bitmap
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

sealed class HomeUiEvent : UiEvent() {
    data object OnAddTransactionClicked : HomeUiEvent()
    data object OnAddIncomeClicked : HomeUiEvent()
    data object OnSeeAllClicked : HomeUiEvent()
}