package com.example.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Produk(
    val id: String = "",
    val nama: String = "",
    val harga: Double = 0.0,
    val satuan: String = "",
    val stok: Double = 0.0,
    val status: String = "aktif",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String? = null
)