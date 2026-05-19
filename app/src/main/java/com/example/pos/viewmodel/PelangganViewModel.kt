package com.example.pos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos.model.CreatePelangganRequest
import com.example.pos.model.Pelanggan
import com.example.pos.model.UpdatePelangganRequest
import com.example.pos.repository.PelangganRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PelangganViewModel(
    private val repository: PelangganRepository = PelangganRepository()
) : ViewModel() {

    // Backing properties untuk StateFlow internal (Mutable)
    private val _listState = MutableStateFlow(PelangganListUiState())
    val listState: StateFlow<PelangganListUiState> = _listState.asStateFlow()

    private val _formState = MutableStateFlow(PelangganFormUiState())
    val formState: StateFlow<PelangganFormUiState> = _formState.asStateFlow()

    // ════════════════════════════════════════════════════════════════════
    // FUNGSI UNTUK HALAMAN DAFTAR PELANGGAN
    // ════════════════════════════════════════════════════════════════════
    fun loadPelanggan() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = repository.fetchPelanggan()
                _listState.update { it.copy(isLoading = false, pelangganList = result) }
            } catch (e: Exception) {
                _listState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Gagal memuat data pelanggan"
                    )
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // FUNGSI UNTUK FORM (LOAD DATA PELANGGAN BY ID SAAT EDIT)
    // ════════════════════════════════════════════════════════════════════
    fun loadPelangganById(id: String) {
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true, statusMessage = null) }
            try {
                val pelanggan = repository.fetchPelangganById(id)
                _formState.update { it.copy(isLoading = false, selectedPelanggan = pelanggan) }
            } catch (e: Exception) {
                _formState.update {
                    it.copy(
                        isLoading = false,
                        statusMessage = "❌ Gagal memuat data: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // FUNGSI SIMPAN/UPDATE PELANGGAN (UPSERT)
    // ════════════════════════════════════════════════════════════════════
    fun upsertPelanggan(
        id: String? = null,
        nama: String,
        noHp: String,
        alamat: String,
        email: String,
        status: String = "aktif"
    ) {
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true, statusMessage = null, isSuccess = false) }
            try {
                if (id == null) {
                    // ── TAMBAH PELANGGAN BARU ──
                    repository.createPelanggan(
                        CreatePelangganRequest(
                            p_nama = nama,
                            p_no_hp = noHp,
                            p_alamat = alamat.ifEmpty { null },
                            p_email = email.ifEmpty { null }
                        )
                    )
                    _formState.update {
                        it.copy(
                            isLoading = false,
                            statusMessage = "✅ Pelanggan berhasil ditambahkan",
                            isSuccess = true
                        )
                    }
                } else {
                    // ── UPDATE DATA PELANGGAN EXISTING ──
                    repository.updatePelanggan(
                        UpdatePelangganRequest(
                            p_id = id,
                            p_nama = nama,
                            p_no_hp = noHp,
                            p_alamat = alamat.ifEmpty { null },
                            p_email = email.ifEmpty { null },
                            p_status = status
                        )
                    )
                    _formState.update {
                        it.copy(
                            isLoading = false,
                            statusMessage = "✅ Pelanggan berhasil diperbarui",
                            isSuccess = true
                        )
                    }
                }
            } catch (e: Exception) {
                _formState.update {
                    it.copy(
                        isLoading = false,
                        statusMessage = "❌ Gagal menyimpan: ${e.localizedMessage}",
                        isSuccess = false
                    )
                }
            }
        }
    }

    // Reset status message setelah ditampilkan ke layar
    fun clearMessage() {
        _formState.update { it.copy(statusMessage = null) }
    }
}