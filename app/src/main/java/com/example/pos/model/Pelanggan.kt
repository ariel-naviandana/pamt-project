package com.example.pos.model

import kotlinx.serialization.Serializable

@Serializable
data class CreatePelangganRequest(
    val p_nama: String,
    val p_no_hp: String,
    val p_alamat: String? = null,
    val p_email: String? = null
)

@Serializable
data class UpdatePelangganRequest(
    val p_id: String,
    val p_nama: String,
    val p_no_hp: String,
    val p_alamat: String?,
    val p_email: String?,
    val p_status: String
)

@Serializable
data class Pelanggan(
    val id: String,
    val nama: String,
    val no_hp: String? = null,
    val alamat: String? = null,
    val email: String? = null,
    val status: String,
    val created_at: String
)