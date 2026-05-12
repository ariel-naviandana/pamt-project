package com.example.pos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos.repository.PengeluaranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PengeluaranViewModel : ViewModel() {
    private val repository = PengeluaranRepository()

    private val _listState = MutableStateFlow(PengeluaranListState())
    val listState: StateFlow<PengeluaranListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(PengeluaranDetailState())
    val detailState: StateFlow<PengeluaranDetailState> = _detailState.asStateFlow()

    private val _uiState = MutableStateFlow<PengeluaranUiState>(PengeluaranUiState.Idle)
    val uiState: StateFlow<PengeluaranUiState> = _uiState.asStateFlow()

    private val _kasListState = MutableStateFlow(KasListState())
    val kasListState: StateFlow<KasListState> = _kasListState.asStateFlow()

    // Simpan role agar tidak perlu pass terus dari UI
    private var isAdmin: Boolean = false

    // ── INIT ──────────────────────────────────────────────────────────────
    fun init(isAdmin: Boolean) {
        this.isAdmin = isAdmin
        loadPengeluaran()
        loadKasAktif()
    }

    // ── LIST ──────────────────────────────────────────────────────────────
    fun loadPengeluaran() {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)
            try {
                val list = repository.getPengeluaranList(isAdmin)
                _listState.value = PengeluaranListState(pengeluaranList = list)
            } catch (e: Exception) {
                android.util.Log.e("PengeluaranVM", "Error: ${e.message}", e) // tambah ini
                _listState.value = _listState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Gagal memuat pengeluaran"
                )
            }
        }
    }

    // ── DETAIL ────────────────────────────────────────────────────────────
    fun loadDetail(id: String) {
        viewModelScope.launch {
            _detailState.value = PengeluaranDetailState(isLoading = true)
            try {
                val data = repository.getPengeluaranById(id)
                _detailState.value = PengeluaranDetailState(pengeluaran = data)
            } catch (e: Exception) {
                _detailState.value = PengeluaranDetailState(
                    error = e.message ?: "Gagal memuat detail"
                )
            }
        }
    }

    // ── KAS ───────────────────────────────────────────────────────────────
    fun loadKasAktif() {
        viewModelScope.launch {
            _kasListState.value = KasListState(isLoading = true)
            try {
                val list = repository.getKasAktif()
                _kasListState.value = KasListState(kasList = list)
            } catch (e: Exception) {
                _kasListState.value = KasListState(
                    error = e.message ?: "Gagal memuat kas"
                )
            }
        }
    }

    // ── TAMBAH ────────────────────────────────────────────────────────────
    fun tambahPengeluaran(kasId: String, deskripsi: String, nominal: Double) {
        viewModelScope.launch {
            _uiState.value = PengeluaranUiState.Loading
            try {
                repository.tambahPengeluaran(kasId, deskripsi, nominal)
                _uiState.value = PengeluaranUiState.Success
                loadPengeluaran()
            } catch (e: Exception) {
                _uiState.value = PengeluaranUiState.Error(e.message ?: "Gagal tambah pengeluaran")
            }
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────────────
    fun updatePengeluaran(id: String, kasId: String, deskripsi: String, nominal: Double) {
        viewModelScope.launch {
            _uiState.value = PengeluaranUiState.Loading
            try {
                repository.updatePengeluaran(id, kasId, deskripsi, nominal)
                _uiState.value = PengeluaranUiState.Success
                loadPengeluaran()
            } catch (e: Exception) {
                _uiState.value = PengeluaranUiState.Error(e.message ?: "Gagal update pengeluaran")
            }
        }
    }

    // ── APPROVE ───────────────────────────────────────────────────────────
    fun approvePengeluaran(id: String) {
        viewModelScope.launch {
            _uiState.value = PengeluaranUiState.Loading
            try {
                repository.approvePengeluaran(id)
                _uiState.value = PengeluaranUiState.Success
                loadDetail(id)
                loadPengeluaran()
            } catch (e: Exception) {
                _uiState.value = PengeluaranUiState.Error(e.message ?: "Gagal approve pengeluaran")
            }
        }
    }

    // ── BATALKAN ──────────────────────────────────────────────────────────
    fun batalkanPengeluaran(id: String) {
        viewModelScope.launch {
            _uiState.value = PengeluaranUiState.Loading
            try {
                repository.batalkanPengeluaran(id)
                _uiState.value = PengeluaranUiState.Success
                loadDetail(id)
                loadPengeluaran()
            } catch (e: Exception) {
                _uiState.value = PengeluaranUiState.Error(e.message ?: "Gagal batalkan pengeluaran")
            }
        }
    }

    // ── RESET ─────────────────────────────────────────────────────────────
    fun resetUiState() { _uiState.value = PengeluaranUiState.Idle }
}