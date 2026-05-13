package com.example.pos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos.model.Kas
import com.example.pos.repository.KasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class KasListState(
    val isLoading: Boolean = false,
    val kasList: List<Kas> = emptyList(),
    val error: String? = null
)

sealed class KasUiState {
    object Idle : KasUiState()
    object Loading : KasUiState()
    object Success : KasUiState()
    data class Error(val message: String) : KasUiState()
}

class KasViewModel : ViewModel() {
    private val repository = KasRepository()

    private val _listState = MutableStateFlow(KasListState())
    val listState: StateFlow<KasListState> = _listState.asStateFlow()

    private val _uiState = MutableStateFlow<KasUiState>(KasUiState.Idle)
    val uiState: StateFlow<KasUiState> = _uiState.asStateFlow()

    private var currentUserRole: String = "cashier"

    fun init(isAdmin: Boolean) {
        this.currentUserRole = if (isAdmin) "admin" else "cashier"
        fetchKas()
    }

    fun fetchKas() {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)
            try {
                val kasList = repository.getKasList(currentUserRole)
                _listState.value = KasListState(kasList = kasList)
            } catch (e: Exception) {
                _listState.value = _listState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Gagal memuat data kas"
                )
            }
        }
    }

    fun addKas(nama: String, saldo: String) {
        viewModelScope.launch {
            _uiState.value = KasUiState.Loading
            try {
                val saldoDouble = saldo.toDoubleOrNull() ?: 0.0
                repository.insertKas(nama, saldoDouble)
                _uiState.value = KasUiState.Success
                fetchKas()
            } catch (e: Exception) {
                _uiState.value = KasUiState.Error("Gagal menyimpan: ${e.message}")
            }
        }
    }

    fun updateKasAndAdjust(
        id: String,
        nama: String,
        currentSaldo: Double,
        adjustmentType: String?,
        adjustmentNominal: String
    ) {
        viewModelScope.launch {
            _uiState.value = KasUiState.Loading
            try {
                // 1. Update Nama
                repository.updateKas(id, nama, currentSaldo)
                
                // 2. Adjust Saldo jika ada nominal
                val nominalDouble = adjustmentNominal.toDoubleOrNull() ?: 0.0
                if (nominalDouble > 0 && adjustmentType != null) {
                    repository.adjustSaldo(id, adjustmentType, nominalDouble)
                }
                
                _uiState.value = KasUiState.Success
                fetchKas()
            } catch (e: Exception) {
                _uiState.value = KasUiState.Error("Gagal update: ${e.message}")
            }
        }
    }

    fun deleteKas(id: String) {
        viewModelScope.launch {
            _uiState.value = KasUiState.Loading
            try {
                repository.deleteKas(id)
                _uiState.value = KasUiState.Success
                fetchKas()
            } catch (e: Exception) {
                _uiState.value = KasUiState.Error("Gagal nonaktifkan: ${e.message}")
            }
        }
    }

    fun activateKas(id: String) {
        viewModelScope.launch {
            _uiState.value = KasUiState.Loading
            try {
                repository.activateKas(id)
                _uiState.value = KasUiState.Success
                fetchKas()
            } catch (e: Exception) {
                _uiState.value = KasUiState.Error("Gagal mengaktifkan: ${e.message}")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = KasUiState.Idle
    }
}
