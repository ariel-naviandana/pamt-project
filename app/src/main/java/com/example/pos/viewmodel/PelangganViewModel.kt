package com.example.pos.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos.model.CreatePelangganRequest
import com.example.pos.model.Pelanggan
import com.example.pos.model.UpdatePelangganRequest
import com.example.pos.repository.PelangganRepository
import kotlinx.coroutines.launch

class PelangganViewModel(private val repository: PelangganRepository) : ViewModel() {

    var isLoading by mutableStateOf(false)
    var statusMessage by mutableStateOf<String?>(null)

    // State untuk List Pelanggan
    private val _pelangganList = mutableStateListOf<Pelanggan>()
    val pelangganList: List<Pelanggan> get() = _pelangganList

    // Fungsi Load Data
    fun loadPelanggan() {
        viewModelScope.launch {
            isLoading = true
            try {
                val data = repository.fetchPelanggan()
                _pelangganList.clear()
                _pelangganList.addAll(data)
            } catch (e: Exception) {
                statusMessage = "Gagal memuat data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun upsertPelanggan(
        id: String? = null,
        nama: String,
        noHp: String,
        alamat: String,
        email: String,
        status: String = "aktif"
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                if (id == null) {
                    // Logika Tambah Pelanggan [cite: 19]
                    repository.createPelanggan(
                        CreatePelangganRequest(nama, noHp, alamat.ifEmpty { null }, email.ifEmpty { null })
                    )
                } else {
                    // Logika Ubah Pelanggan [cite: 20]
                    repository.updatePelanggan(
                        UpdatePelangganRequest(id, nama, noHp, alamat.ifEmpty { null }, email.ifEmpty { null }, status)
                    )
                }
                statusMessage = "Berhasil menyimpan data pelanggan"
            } catch (e: Exception) {
                statusMessage = "Gagal: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearMessage() { statusMessage = null }
}