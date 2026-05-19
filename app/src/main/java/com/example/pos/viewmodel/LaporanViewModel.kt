package com.example.pos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos.repository.LaporanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LaporanViewModel(
    private val repository: LaporanRepository = LaporanRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<LaporanUiState>(LaporanUiState.Loading)
    val uiState: StateFlow<LaporanUiState> = _uiState.asStateFlow()

    init {
        loadLaporan()
    }

    fun loadLaporan() {
        viewModelScope.launch {
            _uiState.value = LaporanUiState.Loading
            try {
                val data = repository.getLabaRugiAllTime()
                _uiState.value = LaporanUiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = LaporanUiState.Error(
                    message = e.localizedMessage ?: "Gagal mengambil data laporan keuangan"
                )
            }
        }
    }
}