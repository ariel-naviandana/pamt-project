package com.example.pos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos.model.Kas
import com.example.pos.repository.KasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class KasUiState {
    object Loading : KasUiState()
    data class Success(val data: List<Kas>) : KasUiState()
    data class Error(val message: String) : KasUiState()
}

class KasViewModel : ViewModel() {
    private val repository = KasRepository()

    private val _uiState = MutableStateFlow<KasUiState>(KasUiState.Loading)
    val uiState: StateFlow<KasUiState> = _uiState

    private val _selectedKas = MutableStateFlow<Kas?>(null)
    val selectedKas: StateFlow<Kas?> = _selectedKas

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog

    private var currentUserRole: String = "cashier"

    // Mendapatkan daftar kas
    fun fetchKas(role: String) {
        currentUserRole = role // Update role yang sedang aktif
        viewModelScope.launch {
            _uiState.value = KasUiState.Loading
            try {
                val kasList = repository.getKasList(role)
                _uiState.value = KasUiState.Success(kasList)
            } catch (e: Exception) {
                _uiState.value = KasUiState.Error(e.message ?: "Gagal memuat data kas")
            }
        }
    }

    fun setShowAddDialog(show: Boolean) {
        _showAddDialog.value = show
    }

    // Tambah kas baru
    fun addKas(nama: String, saldo: String) {
        viewModelScope.launch {
            _uiState.value = KasUiState.Loading
            try {
                val saldoDouble = saldo.toDoubleOrNull() ?: 0.0
                repository.insertKas(nama, saldoDouble)
                _showAddDialog.value = false

                // Gunakan currentUserRole yang sudah disimpan
                fetchKas(currentUserRole)

            } catch (e: Exception) {
                _uiState.value = KasUiState.Error("Gagal menyimpan: ${e.message}")
            }
        }
    }

    fun selectKas(kas: Kas?) {
        _selectedKas.value = kas
    }

    // Update nama dan saldo kas
    fun updateKas(id: String, nama: String, saldo: String) {
        viewModelScope.launch {
            _uiState.value = KasUiState.Loading
            try {
                val saldoDouble = saldo.toDoubleOrNull() ?: 0.0
                repository.updateKas(id, nama, saldoDouble)
                _selectedKas.value = null

                // Gunakan currentUserRole untuk refresh
                fetchKas(currentUserRole)
            } catch (e: Exception) {
                _uiState.value = KasUiState.Error("Gagal update: ${e.message}")
            }
        }
    }

    // Soft delete kas
    fun deleteKas(id: String) {
        viewModelScope.launch {
            _uiState.value = KasUiState.Loading
            try {
                repository.deleteKas(id)
                _selectedKas.value = null

                // Gunakan currentUserRole untuk refresh
                fetchKas(currentUserRole)
            } catch (e: Exception) {
                _uiState.value = KasUiState.Error("Gagal hapus: ${e.message}")
            }
        }
    }

    // Mengaktifkan kembali kas yang dinonaktifkan
    fun activateKas(id: String) {
        _uiState.value = KasUiState.Loading
        viewModelScope.launch {
            try {
                repository.activateKas(id)
                _selectedKas.value = null // Tutup dialog
                fetchKas(currentUserRole) // Refresh daftar kas
            } catch (e: Exception) {
                _uiState.value = KasUiState.Error("Gagal mengaktifkan: ${e.message}")
            }
        }
    }
}