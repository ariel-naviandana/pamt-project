package com.example.pos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos.repository.PenjualanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PenjualanViewModel : ViewModel() {
    private val repository = PenjualanRepository()

    private val _listState = MutableStateFlow(PenjualanListState())
    val listState: StateFlow<PenjualanListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(PenjualanDetailState())
    val detailState: StateFlow<PenjualanDetailState> = _detailState.asStateFlow()

    private val _uiState = MutableStateFlow<PenjualanUiState>(PenjualanUiState.Idle)
    val uiState: StateFlow<PenjualanUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(PenjualanFormState())
    val formState: StateFlow<PenjualanFormState> = _formState.asStateFlow()

    private val _kasListState = MutableStateFlow(KasSimpleListState())
    val kasListState: StateFlow<KasSimpleListState> = _kasListState.asStateFlow()

    private val _activeDraftId = MutableStateFlow<String?>(null)
    val activeDraftId: StateFlow<String?> = _activeDraftId.asStateFlow()

    private var isAdmin: Boolean = false

    // ── INIT ──────────────────────────────────────────────────────────────
    fun init(isAdmin: Boolean) {
        this.isAdmin = isAdmin
        loadPenjualan()
    }

    // ── LIST ──────────────────────────────────────────────────────────────
    fun loadPenjualan() {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)
            try {
                val list = repository.getPenjualanList(isAdmin)
                _listState.value = PenjualanListState(penjualanList = list)
            } catch (e: Exception) {
                _listState.value = _listState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Gagal memuat penjualan"
                )
            }
        }
    }

    // ── DETAIL ────────────────────────────────────────────────────────────
    fun loadDetail(penjualanId: String) {
        viewModelScope.launch {
            _detailState.value = PenjualanDetailState(isLoading = true)
            try {
                val data = repository.getPenjualanDetail(penjualanId)
                _detailState.value = PenjualanDetailState(penjualan = data)
            } catch (e: Exception) {
                _detailState.value = PenjualanDetailState(
                    error = e.message ?: "Gagal memuat detail penjualan"
                )
            }
        }
    }

    // ── FORM DATA ─────────────────────────────────────────────────────────
    fun loadFormData() {
        viewModelScope.launch {
            _formState.value = PenjualanFormState(isLoading = true)
            try {
                val pelanggan = repository.getPelangganAktif()
                val produk = repository.getProdukAktif()
                _formState.value = PenjualanFormState(
                    pelangganList = pelanggan,
                    produkList = produk
                )
            } catch (e: Exception) {
                _formState.value = PenjualanFormState(
                    error = e.message ?: "Gagal memuat data form"
                )
            }
        }
        loadKasAktif()
    }

    // ── KAS ───────────────────────────────────────────────────────────────
    fun loadKasAktif() {
        viewModelScope.launch {
            _kasListState.value = KasSimpleListState(isLoading = true)
            try {
                val list = repository.getKasAktif()
                _kasListState.value = KasSimpleListState(kasList = list)
            } catch (e: Exception) {
                _kasListState.value = KasSimpleListState(
                    error = e.message ?: "Gagal memuat kas"
                )
            }
        }
    }

    // ── CREATE DRAFT ──────────────────────────────────────────────────────
    fun createDraft(pelangganId: String, kasId: String) {
        viewModelScope.launch {
            _uiState.value = PenjualanUiState.Loading
            try {
                val id = repository.createPenjualan(pelangganId, kasId)
                _activeDraftId.value = id
                loadDetail(id)
                _uiState.value = PenjualanUiState.Idle
            } catch (e: Exception) {
                _uiState.value = PenjualanUiState.Error(e.message ?: "Gagal membuat transaksi")
            }
        }
    }

    // ── ITEM MANAGEMENT ───────────────────────────────────────────────────
    fun addItem(produkId: String, qty: Int, hargaSatuan: Double) {
        val penjualanId = _activeDraftId.value ?: return
        viewModelScope.launch {
            _uiState.value = PenjualanUiState.Loading
            try {
                repository.addItem(penjualanId, produkId, qty, hargaSatuan)
                loadDetail(penjualanId)
                _uiState.value = PenjualanUiState.Idle
            } catch (e: Exception) {
                _uiState.value = PenjualanUiState.Error(e.message ?: "Gagal menambah item")
            }
        }
    }

    fun removeItem(detailId: String) {
        val penjualanId = _activeDraftId.value ?: return
        viewModelScope.launch {
            _uiState.value = PenjualanUiState.Loading
            try {
                repository.removeItem(detailId)
                loadDetail(penjualanId)
                _uiState.value = PenjualanUiState.Idle
            } catch (e: Exception) {
                _uiState.value = PenjualanUiState.Error(e.message ?: "Gagal menghapus item")
            }
        }
    }

    // ── SELESAIKAN ────────────────────────────────────────────────────────
    fun selesaikanPenjualan() {
        val penjualanId = _activeDraftId.value ?: return
        viewModelScope.launch {
            _uiState.value = PenjualanUiState.Loading
            try {
                repository.selesaikanPenjualan(penjualanId)
                _uiState.value = PenjualanUiState.Success
                _activeDraftId.value = null
                loadPenjualan()
            } catch (e: Exception) {
                _uiState.value = PenjualanUiState.Error(e.message ?: "Gagal menyelesaikan penjualan")
            }
        }
    }

    // ── BATALKAN ──────────────────────────────────────────────────────────
    fun batalkanPenjualan(penjualanId: String) {
        viewModelScope.launch {
            _uiState.value = PenjualanUiState.Loading
            try {
                repository.batalkanPenjualan(penjualanId)
                _uiState.value = PenjualanUiState.Success
                if (_activeDraftId.value == penjualanId) _activeDraftId.value = null
                loadPenjualan()
            } catch (e: Exception) {
                _uiState.value = PenjualanUiState.Error(e.message ?: "Gagal membatalkan penjualan")
            }
        }
    }

    // ── RESET ─────────────────────────────────────────────────────────────
    fun resetUiState() { _uiState.value = PenjualanUiState.Idle }
    fun resetDraft() { _activeDraftId.value = null }
}