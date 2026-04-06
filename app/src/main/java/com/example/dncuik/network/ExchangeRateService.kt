package com.example.dncuik.network

import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateService {
    @GET("v6/81a967530be90875e54c7d5c/latest/USD")
    suspend fun getLatestRates(): retrofit2.Response<ExchangeRateResponse>
}
