package com.example.pos.viewmodel

import com.example.pos.model.KasSimple
import com.example.pos.model.Pelanggan
import com.example.pos.model.PenjualanWithRelasi
import com.example.pos.model.Produk

sealed class PenjualanUiState {
    object Idle : PenjualanUiState()
    object Loading : PenjualanUiState()
    object Success : PenjualanUiState()
    data class Error(val message: String) : PenjualanUiState()
}

data class PenjualanListState(
    val penjualanList: List<PenjualanWithRelasi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class PenjualanDetailState(
    val penjualan: PenjualanWithRelasi? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class PenjualanFormState(
    val pelangganList: List<Pelanggan> = emptyList(),
    val produkList: List<Produk> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// KasSimpleListState dipakai bersama dengan PengeluaranUiState
// Pastikan tidak duplikat — cukup satu definisi di PengeluaranUiState.kt