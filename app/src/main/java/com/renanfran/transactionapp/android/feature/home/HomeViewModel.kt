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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dao: TransactionDao,
    private val imageDao: RandomImageDao
) : BaseViewModel() {

    val expenses = dao.getAllExpense()
    private val _randomImageUrl = MutableStateFlow<Bitmap?>(null)
    val randomImageUrl: StateFlow<Bitmap?> = _randomImageUrl.asStateFlow()

    init {
        fetchRandomImage()
    }

    override fun onEvent(event: UiEvent) {
        when (event) {
            is HomeUiEvent.OnAddTransactionClicked -> {
                navigateTo(HomeNavigationEvent.NavigateToAddTransaction)
            }
            is HomeUiEvent.OnAddIncomeClicked -> {
                navigateTo(HomeNavigationEvent.NavigateToAddIncome)
            }
            is HomeUiEvent.OnSeeAllClicked -> {
                navigateTo(HomeNavigationEvent.NavigateToSeeAll)
            }
        }
    }

    private fun navigateTo(event: HomeNavigationEvent) {
        viewModelScope.launch {
            _navigationEvent.emit(event)
        }
    }

    fun getBalance(list: List<TransactionEntity>): String {
        val balance = list.sumOf { expense ->
            if (expense.type == "Receita") expense.amount else -expense.amount
        }
        return Utils.formatCurrency(balance)
    }

    fun getTotalExpense(list: List<TransactionEntity>): String {
        val total = list.filter { it.type != "Receita" }.sumOf { it.amount }
        return Utils.formatCurrency(total)
    }

    fun getTotalIncome(list: List<TransactionEntity>): String {
        val totalIncome = list.filter { it.type == "Receita" }.sumOf { it.amount }
        return Utils.formatCurrency(totalIncome)
    }

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
                val imageBytes = responseBody.bytes()
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                _randomImageUrl.value = bitmap
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveImage(image: Bitmap?) {
        if (image != null) {
            viewModelScope.launch {
                try {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val byteArray = byteArrayOutputStream.toByteArray()

                    val imageEntity = RandomImageEntity(imageBitmap = byteArray)
                    imageDao.insertImage(imageEntity)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun setRandomImage(bitmap: Bitmap) {
        _randomImageUrl.value = bitmap
    }
}

sealed class HomeUiEvent : UiEvent() {
    data object OnAddTransactionClicked : HomeUiEvent()
    data object OnAddIncomeClicked : HomeUiEvent()
    data object OnSeeAllClicked : HomeUiEvent()
}
