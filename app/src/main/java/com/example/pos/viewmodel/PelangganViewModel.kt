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

class PelangganViewModel(
    private val repository: PelangganRepository = PelangganRepository()
) : ViewModel() {

    var isLoading by mutableStateOf(false)
    var statusMessage by mutableStateOf<String?>(null)

    // ════════════════════════════════════════════════════════════════════
    // State untuk List Pelanggan
    // ════════════════════════════════════════════════════════════════════
    private val _pelangganList = mutableStateListOf<Pelanggan>()
    val pelangganList: List<Pelanggan> get() = _pelangganList

    // ════════════════════════════════════════════════════════════════════
    // Fungsi Load Data Pelanggan
    // ════════════════════════════════════════════════════════════════════
    fun loadPelanggan() {
        viewModelScope.launch {
            isLoading = true
            try {
                val data = repository.fetchPelanggan()
                _pelangganList.clear()
                _pelangganList.addAll(data)
                statusMessage = null // Clear message jika berhasil
            } catch (e: Exception) {
                statusMessage = "❌ Gagal memuat data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // Fungsi Tambah/Update Pelanggan (Upsert)
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
            isLoading = true
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
                    statusMessage = "✅ Pelanggan berhasil ditambahkan"
                    // Reload list setelah tambah
                    loadPelanggan()
                } else {
                    // ── UPDATE PELANGGAN YANG SUDAH ADA ──
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
                    statusMessage = "✅ Pelanggan berhasil diperbarui"
                    // Reload list setelah update
                    loadPelanggan()
                }
            } catch (e: Exception) {
                statusMessage = "❌ Gagal: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // Fungsi Clear Message
    // ════════════════════════════════════════════════════════════════════
    fun clearMessage() {
        statusMessage = null
    }
}