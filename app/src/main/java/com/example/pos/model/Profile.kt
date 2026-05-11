package com.example.pos.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val nama: String? = null,
    val role: String
)