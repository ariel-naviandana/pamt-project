package com.example.pos.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String = "",
    val nama: String? = null,
    val role: String = "cashier"
)