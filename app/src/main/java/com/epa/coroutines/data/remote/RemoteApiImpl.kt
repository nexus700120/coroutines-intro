package com.epa.coroutines.data.remote

import com.epa.coroutines.domain.ApiException
import com.epa.coroutines.domain.RemoteApi
import com.epa.coroutines.domain.entity.CurrencyPair
import com.epa.coroutines.domain.entity.Rate
import java.util.*

class RemoteApiImpl(private val rest: FcApi) : RemoteApi {

    override suspend fun getCurrencyPairs(): List<CurrencyPair> =
        rest.getCurrencyPairs().resultOrError()

    override suspend fun getRates(currencyPairId: Int): List<Rate> =
        rest.getRates(currencyPairId)
            .resultOrError()
            .values.map { Rate(Date(it.t), it.c.toDouble()) }

    private fun <T> ApiResponse<T>.resultOrError() : T =
        if (status) response else throw ApiException(msg)
}