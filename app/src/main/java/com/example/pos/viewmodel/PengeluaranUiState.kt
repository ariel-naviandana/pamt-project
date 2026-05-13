package com.example.pos.viewmodel

import com.example.pos.model.KasSimple
import com.example.pos.model.PengeluaranWithKas

sealed class PengeluaranUiState {
    object Idle : PengeluaranUiState()
    object Loading : PengeluaranUiState()
    object Success : PengeluaranUiState()
    data class Error(val message: String) : PengeluaranUiState()
}

data class PengeluaranListState(
    val pengeluaranList: List<PengeluaranWithKas> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class PengeluaranDetailState(
    val pengeluaran: PengeluaranWithKas? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class KasListState(
    val kasList: List<KasSimple> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)