package com.example.pos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos.model.BulanFilter
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

    // State untuk menyimpan daftar filter bulan dari Supabase
    private val _listFilter = MutableStateFlow<List<BulanFilter>>(emptyList())
    val listFilter: StateFlow<List<BulanFilter>> = _listFilter.asStateFlow()

    // State untuk menyimpan filter yang sedang dipilih saat ini
    private val _filterTerpilih = MutableStateFlow(BulanFilter("Semua Waktu", null, null))
    val filterTerpilih: StateFlow<BulanFilter> = _filterTerpilih.asStateFlow()

    init {
        loadDataAwal()
    }

    private fun loadDataAwal() {
        viewModelScope.launch {
            try {
                // 1. Ambil daftar bulan dari database
                val bulanDariDb = repository.getDaftarBulan()

                // 2. Tambahkan opsi "Semua Waktu" di urutan paling atas
                val kombinasiFilter = listOf(BulanFilter("Semua Waktu", null, null)) + bulanDariDb
                _listFilter.value = kombinasiFilter
                _filterTerpilih.value = kombinasiFilter.first()

                // 3. Load laporan berdasarkan opsi "Semua Waktu"
                loadLaporan(kombinasiFilter.first())
            } catch (e: Exception) {
                _uiState.value = LaporanUiState.Error("Gagal memuat filter: ${e.localizedMessage}")
            }
        }
    }

    fun loadLaporan(filter: BulanFilter) {
        viewModelScope.launch {
            _filterTerpilih.value = filter
            _uiState.value = LaporanUiState.Loading
            try {
                val data = repository.getLabaRugi(filter.startDate, filter.endDate)
                _uiState.value = LaporanUiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = LaporanUiState.Error(
                    e.localizedMessage ?: "Gagal mengambil data laporan keuangan"
                )
            }
        }
    }
}