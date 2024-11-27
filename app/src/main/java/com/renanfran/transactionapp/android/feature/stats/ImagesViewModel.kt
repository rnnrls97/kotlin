package com.renanfran.transactionapp.android.feature.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.renanfran.transactionapp.android.base.BaseViewModel
import com.renanfran.transactionapp.android.base.UiEvent
import com.renanfran.transactionapp.android.utils.Utils
import com.renanfran.transactionapp.android.data.dao.TransactionDao
import com.renanfran.transactionapp.android.data.model.TransactionSummary
import com.github.mikephil.charting.data.Entry
import com.renanfran.transactionapp.android.data.dao.RandomImageDao
import com.renanfran.transactionapp.android.data.model.RandomImageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val imageDao: RandomImageDao
) : ViewModel() {

    val images: StateFlow<List<RandomImageEntity>> = imageDao.getAllImages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun deleteImage(imageEntity: RandomImageEntity) {
        viewModelScope.launch {
            imageDao.deleteImage(imageEntity)
        }
    }
}