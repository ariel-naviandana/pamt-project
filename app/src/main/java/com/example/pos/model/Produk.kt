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

@Serializable
data class CreateProdukRequest(
    val p_nama: String,
    val p_harga: Double,
    val p_satuan: String,
    val p_stok: Double
)

@Serializable
data class UpdateProdukRequest(
    val p_id: String,
    val p_nama: String,
    val p_harga: Double,
    val p_satuan: String
)

@Serializable
data class UpdateStokManualRequest(
    val p_produk_id: String,
    val p_tipe: String,
    val p_qty: Int
)

@Serializable
data class ToggleStatusProdukRequest(
    val p_id: String
)