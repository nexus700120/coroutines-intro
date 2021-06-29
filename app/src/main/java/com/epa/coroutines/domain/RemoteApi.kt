package com.epa.coroutines.domain

import com.epa.coroutines.domain.entity.CurrencyPair
import com.epa.coroutines.domain.entity.Rate

interface RemoteApi {
    suspend fun getCurrencyPairs(): List<CurrencyPair>

    suspend fun getRates(currencyPairId: Int): List<Rate>
}