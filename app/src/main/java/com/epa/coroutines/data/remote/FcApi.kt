package com.epa.coroutines.data.remote

import com.epa.coroutines.domain.entity.CurrencyPair
import retrofit2.http.GET
import retrofit2.http.Query

interface FcApi {
    @GET("api-v3/crypto/list?type=crypto")
    suspend fun getCurrencyPairs(): ApiResponse<List<CurrencyPair>>

    @GET("api-v3/crypto/history?period=1h")
    suspend fun getRates(@Query("id") currencyPairId: Int): ApiResponse<Map<String, RatesRaw>>
}