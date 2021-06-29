package com.epa.coroutines.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epa.coroutines.domain.LocalStorage
import com.epa.coroutines.domain.RemoteApi
import com.epa.coroutines.domain.entity.CurrencyPair
import com.epa.coroutines.domain.entity.Rate
import kotlinx.coroutines.*

class DashboardViewModel(
    private val remoteApi: RemoteApi,
    private val localStorage: LocalStorage
) : ViewModel() {

    val loading = MutableLiveData<Boolean>()

    val selectedPair = MutableLiveData<CurrencyPair>()

    val pairs = mutableListOf<CurrencyPair>()

    val rates = MutableLiveData<List<Rate>>()

    val error = MutableLiveData<String>()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            loading.value = true

            val savedIdDeferred = async(Dispatchers.IO) { localStorage.getInt(KEY) }
            val pairsDeferred = async(Dispatchers.IO) { remoteApi.getCurrencyPairs() }

            pairs.clear()
            pairs.addAll(pairsDeferred.await())
            val savedId = savedIdDeferred.await()

            if (savedId == null) {
                val selected = pairs.first()
                launch { saveCurrencyPair(selected) }
                selectedPair.value = selected
            } else {
                selectedPair.value = pairs.first { it.id == savedId }
            }

            rates.value = withContext(Dispatchers.IO) {
                remoteApi.getRates(selectedPair.value!!.id)
            }
            loading.value = false
        }
    }

    fun onCurrencyPairChanged(pair: CurrencyPair) {
        selectedPair.value = pair
        viewModelScope.launch {
            loading.value = true
            rates.value = withContext(Dispatchers.IO) {
                remoteApi.getRates(pair.id)
            }
            launch { saveCurrencyPair(pair) }
            loading.value = false
        }
    }

    private suspend fun saveCurrencyPair(pair: CurrencyPair) {
        withContext(Dispatchers.IO) {
            localStorage.saveInt(KEY, pair.id)
        }
    }

    companion object {
        private const val KEY = "saved_id"
    }
}