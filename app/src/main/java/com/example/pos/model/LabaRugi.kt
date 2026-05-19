package com.example.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LabaRugi(
    @SerialName("total_penjualan") val totalPenjualan: Double = 0.0,
    @SerialName("total_pengeluaran") val totalPengeluaran: Double = 0.0,
    @SerialName("laba_rugi") val labaRugi: Double = 0.0
)