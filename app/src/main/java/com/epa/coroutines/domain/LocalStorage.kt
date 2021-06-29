package com.epa.coroutines.domain

interface LocalStorage {
    suspend fun saveInt(key: String, value: Int)
    suspend fun getInt(key: String): Int?
}