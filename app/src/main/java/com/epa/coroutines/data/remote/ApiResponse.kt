package com.epa.coroutines.data.remote

class ApiResponse<T>(
    val code: Int,
    val status: Boolean,
    val msg: String?,
    val response: T
)