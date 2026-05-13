package com.example.pos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos.repository.ProdukRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProdukViewModel : ViewModel() {
    private val repository = ProdukRepository()

    // ── List State ────────────────────────────────────────────────────────
    private val _listState = MutableStateFlow(ProdukListState())
    val listState: StateFlow<ProdukListState> = _listState.asStateFlow()

    // ── Detail State ──────────────────────────────────────────────────────
    private val _detailState = MutableStateFlow(ProdukDetailState())
    val detailState: StateFlow<ProdukDetailState> = _detailState.asStateFlow()

    // ── Action State (tambah/edit/stok/toggle) ────────────────────────────
    private val _uiState = MutableStateFlow<ProdukUiState>(ProdukUiState.Idle)
    val uiState: StateFlow<ProdukUiState> = _uiState.asStateFlow()

    private var isAdmin: Boolean = false

    fun init(isAdmin: Boolean) {
        this.isAdmin = isAdmin
        loadProduk()
    }

    // ── LIST ──────────────────────────────────────────────────────────────
    fun loadProduk() {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)
            try {
                val list = repository.getAllProduk()
                _listState.value = ProdukListState(produkList = list)
            } catch (e: Exception) {
                _listState.value = _listState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Gagal memuat produk"
                )
            }
        }
    }

    // ── DETAIL ────────────────────────────────────────────────────────────
    fun loadDetail(produkId: String) {
        viewModelScope.launch {
            _detailState.value = ProdukDetailState(isLoading = true)
            try {
                val produk = repository.getProdukById(produkId)
                val log = repository.getLogProduk(produkId)
                _detailState.value = ProdukDetailState(produk = produk, logList = log)
            } catch (e: Exception) {
                _detailState.value = ProdukDetailState(
                    error = e.message ?: "Gagal memuat detail produk"
                )
            }
        }
    }

    // ── TAMBAH ────────────────────────────────────────────────────────────
    fun tambahProduk(nama: String, harga: Double, satuan: String, stok: Double) {
        viewModelScope.launch {
            _uiState.value = ProdukUiState.Loading
            try {
                repository.tambahProduk(nama, harga, satuan, stok)
                _uiState.value = ProdukUiState.Success
                loadProduk()
            } catch (e: Exception) {
                _uiState.value = ProdukUiState.Error(e.message ?: "Gagal menambah produk")
            }
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────────────
    fun updateProduk(id: String, nama: String, harga: Double, satuan: String) {
        viewModelScope.launch {
            _uiState.value = ProdukUiState.Loading
            try {
                repository.updateProduk(id, nama, harga, satuan)
                _uiState.value = ProdukUiState.Success
                loadProduk()
            } catch (e: Exception) {
                _uiState.value = ProdukUiState.Error(e.message ?: "Gagal mengupdate produk")
            }
        }
    }

    // ── STOK MANUAL ───────────────────────────────────────────────────────
    fun updateStokManual(produkId: String, tipe: String, qty: Int) {
        viewModelScope.launch {
            _uiState.value = ProdukUiState.Loading
            try {
                repository.updateStokManual(produkId, tipe, qty)
                _uiState.value = ProdukUiState.Success
                loadDetail(produkId) // Refresh detail + log
            } catch (e: Exception) {
                _uiState.value = ProdukUiState.Error(e.message ?: "Gagal mengupdate stok")
            }
        }
    }

    // ── TOGGLE STATUS ─────────────────────────────────────────────────────
    fun toggleStatusProduk(id: String, statusSaatIni: String) {
        viewModelScope.launch {
            _uiState.value = ProdukUiState.Loading
            try {
                repository.toggleStatusProduk(id, statusSaatIni)
                _uiState.value = ProdukUiState.Success
                loadDetail(id) // Refresh detail
            } catch (e: Exception) {
                _uiState.value = ProdukUiState.Error(e.message ?: "Gagal mengubah status produk")
            }
        }
    }

    // ── RESET ─────────────────────────────────────────────────────────────
    fun resetUiState() {
        _uiState.value = ProdukUiState.Idle
    }
}