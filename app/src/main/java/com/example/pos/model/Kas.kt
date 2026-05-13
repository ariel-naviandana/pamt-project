package com.example.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Kas(
    val id: String,
    val nama: String,
    val status: String,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String? = null,
    val saldo: Double? = 0.0
)

@Serializable
data class KasSimple(
    val id: String,
    val nama: String,
    val saldo: Double? = 0.0,
    val status: String
)

@Serializable
data class CreateKasRequest(
    val p_nama: String,
    val p_saldo: Double
)

@Serializable
data class UpdateKasRequest(
    val p_id: String,
    val p_nama: String,
    val p_saldo: Double
)

@Serializable
data class KasIdRequest(
    val p_id: String
)