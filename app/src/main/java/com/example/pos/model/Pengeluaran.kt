package com.example.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pengeluaran(
    val id: String = "",
    @SerialName("kode_pengeluaran") val kodePengeluaran: String = "",
    val tanggal: String = "",
    @SerialName("kas_id") val kasId: String = "",
    @SerialName("user_id") val userId: String = "",
    val deskripsi: String = "",
    val nominal: Double = 0.0,
    val status: String = "draft",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class PengeluaranInsert(
    @SerialName("kode_pengeluaran") val kodePengeluaran: String,
    @SerialName("kas_id") val kasId: String,
    @SerialName("user_id") val userId: String,
    val deskripsi: String,
    val nominal: Double,
    val status: String = "draft"
)

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
    val kas: Kas? = null
)