package com.example.dncuik.network

import com.google.gson.annotations.SerializedName

data class ExchangeRateResponse(
    val result: String,
    @SerializedName("base_code") val baseCode: String,
    @SerializedName("conversion_rates") val rates: Map<String, Double>
)
