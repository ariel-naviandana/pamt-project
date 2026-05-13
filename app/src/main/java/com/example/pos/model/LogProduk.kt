package com.example.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogProduk(
    val id: String = "",
    @SerialName("produk_id") val produkId: String = "",
    @SerialName("ref_type") val refType: String = "manual",
    @SerialName("ref_id") val refId: String? = null,
    val tipe: String = "",
    val qty: Int = 0,
    @SerialName("created_at") val createdAt: String = ""
)