package com.example.pos.repository

import com.example.pos.data.SupabaseClientProvider
import com.example.pos.model.Pelanggan
import com.example.pos.model.CreatePelangganRequest
import com.example.pos.model.UpdatePelangganRequest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class PelangganRepository {

    // ════════════════════════════════════════════════════════════════════
    // Ambil Supabase Client dari Provider (singleton)
    // ════════════════════════════════════════════════════════════════════
    private val supabase = SupabaseClientProvider.client

    // ════════════════════════════════════════════════════════════════════
    // Ambil List Pelanggan
    // ════════════════════════════════════════════════════════════════════
    suspend fun fetchPelanggan(): List<Pelanggan> {
        return supabase.postgrest.rpc("get_pelanggan").decodeList<Pelanggan>()
    }

    // ════════════════════════════════════════════════════════════════════
    // Ambil Data Pelanggan Berdasarkan ID
    // ════════════════════════════════════════════════════════════════════
    suspend fun fetchPelangganById(id: String): Pelanggan? {
        return try {
            supabase.postgrest.rpc(
                "get_pelanggan_by_id",
                mapOf("p_id" to id)
            ).decodeSingleOrNull<Pelanggan>()
        } catch (e: Exception) {
            null
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // Tambah Pelanggan Baru
    // ════════════════════════════════════════════════════════════════════
    suspend fun createPelanggan(request: CreatePelangganRequest) {
        supabase.postgrest.rpc("create_pelanggan", request)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Update Pelanggan
    // ════════════════════════════════════════════════════════════════════
    suspend fun updatePelanggan(request: UpdatePelangganRequest) {
        supabase.postgrest.rpc("update_pelanggan", request)
    }
}