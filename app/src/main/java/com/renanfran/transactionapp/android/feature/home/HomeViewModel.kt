package com.renanfran.transactionapp.android.feature.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.renanfran.transactionapp.android.base.BaseViewModel
import com.renanfran.transactionapp.android.base.HomeNavigationEvent
import com.renanfran.transactionapp.android.base.UiEvent
import com.renanfran.transactionapp.android.data.dao.RandomImageDao
import com.renanfran.transactionapp.android.utils.Utils
import com.renanfran.transactionapp.android.data.dao.TransactionDao
import com.renanfran.transactionapp.android.data.model.RandomImageEntity
import com.renanfran.transactionapp.android.data.model.TransactionEntity
import com.renanfran.transactionapp.android.data.service.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val imageDao: RandomImageDao
) : BaseViewModel() {

    val expenses: Flow<List<TransactionEntity>> = transactionDao.getAllExpense()

    private val _randomImageUrl = MutableStateFlow<Bitmap?>(null)
    val randomImageUrl: StateFlow<Bitmap?> = _randomImageUrl.asStateFlow()

    init {
        fetchRandomImage()
    }

    override fun onEvent(event: UiEvent) {
        when (event) {
            is HomeUiEvent.OnAddTransactionClicked -> navigateTo(HomeNavigationEvent.NavigateToAddTransaction)
            is HomeUiEvent.OnAddIncomeClicked -> navigateTo(HomeNavigationEvent.NavigateToAddIncome)
            is HomeUiEvent.OnSeeAllClicked -> navigateTo(HomeNavigationEvent.NavigateToSeeAll)
            else -> Unit
        }
    }

    private fun navigateTo(event: HomeNavigationEvent) {
        viewModelScope.launch {
            _navigationEvent.emit(event)
        }
    }

    fun getBalance(transactions: List<TransactionEntity>): String {
        val balance = transactions.sumOf { transaction ->
            if (transaction.type == "Receita") transaction.amount else -transaction.amount
        }
        return Utils.formatCurrency(balance)
    }

    fun getTotalExpense(transactions: List<TransactionEntity>): String {
        val totalExpense = transactions.filter { it.type != "Receita" }.sumOf { it.amount }
        return Utils.formatCurrency(totalExpense)
    }

    fun getTotalIncome(transactions: List<TransactionEntity>): String {
        val totalIncome = transactions.filter { it.type == "Receita" }.sumOf { it.amount }
        return Utils.formatCurrency(totalIncome)
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            runCatching {
                transactionDao.deleteExpense(transaction)
            }.onFailure { e ->
                e.printStackTrace()
            }
        }
    }

    fun fetchRandomImage() {
        viewModelScope.launch {
            runCatching {
                val random = System.currentTimeMillis()
                val responseBody = RetrofitInstance.api.getRandomImage(random)
                val imageBytes = responseBody.bytes()
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            }.onSuccess { bitmap ->
                _randomImageUrl.value = bitmap
            }.onFailure { e ->
                e.printStackTrace()
            }
        }
    }

    fun saveImage(image: Bitmap?) {
        if (image == null) return
        viewModelScope.launch {
            runCatching {
                val byteArrayOutputStream = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                imageDao.insertImage(RandomImageEntity(imageBitmap = byteArray))
            }.onFailure { e ->
                e.printStackTrace()
            }
        }
    }

    fun setRandomImage(bitmap: Bitmap) {
        _randomImageUrl.value = bitmap
    }
}

sealed class HomeUiEvent : UiEvent() {
    object OnAddTransactionClicked : HomeUiEvent()
    object OnAddIncomeClicked : HomeUiEvent()
    object OnSeeAllClicked : HomeUiEvent()
}
