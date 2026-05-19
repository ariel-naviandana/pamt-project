package com.example.pos.viewmodel

import com.example.pos.model.Pelanggan

// State untuk halaman PelangganListScreen
data class PelangganListUiState(
    val isLoading: Boolean = false,
    val pelangganList: List<Pelanggan> = emptyList(),
    val errorMessage: String? = null
)

// State untuk halaman AddEditPelangganScreen
data class PelangganFormUiState(
    val isLoading: Boolean = false,
    val selectedPelanggan: Pelanggan? = null,
    val statusMessage: String? = null,
    val isSuccess: Boolean = false
)