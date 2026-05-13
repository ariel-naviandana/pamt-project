package com.example.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PengeluaranWithKas(
    val id: String = "",
    @SerialName("kode_pengeluaran") val kodePengeluaran: String = "",
    val tanggal: String = "",
    @SerialName("kas_id") val kasId: String = "",
    @SerialName("user_id") val userId: String = "",
    val deskripsi: String = "",
    val nominal: Double = 0.0,
    val status: String = "draft",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String? = null,
    val kas: KasSimple? = null
)

@Serializable
data class CreatePengeluaranRequest(
    val p_kas_id: String,
    val p_deskripsi: String,
    val p_nominal: Double
)

@Serializable
data class UpdatePengeluaranRequest(
    val p_id: String,
    val p_kas_id: String,
    val p_deskripsi: String,
    val p_nominal: Double
)

@Serializable
data class PengeluaranIdRequest(
    val p_id: String
)