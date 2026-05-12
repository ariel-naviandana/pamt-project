package com.example.pos.viewmodel

import com.example.pos.model.LogProduk
import com.example.pos.model.Produk

sealed class ProdukUiState {
    object Idle : ProdukUiState()
    object Loading : ProdukUiState()
    object Success : ProdukUiState()
    data class Error(val message: String) : ProdukUiState()
}

data class ProdukListState(
    val produkList: List<Produk> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ProdukDetailState(
    val produk: Produk? = null,
    val logList: List<LogProduk> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)