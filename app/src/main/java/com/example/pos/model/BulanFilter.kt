package com.example.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BulanFilter(
    val label: String,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null
)