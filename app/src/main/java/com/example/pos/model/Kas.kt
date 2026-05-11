package com.example.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Kas(
    val id: String,
    val nama: String,
    val status: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    val saldo: Double? = 0.0
)

@Serializable
data class KasInsert(
    val nama: String,
    val saldo: Double
)

@Serializable
data class KasUpdate(
    val nama: String,
    val saldo: Double,

    @SerialName("updated_at")
    val updatedAt: String = "now()"
)