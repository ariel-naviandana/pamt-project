package com.example.pos.repository

import com.example.pos.model.Pelanggan
import com.example.pos.model.CreatePelangganRequest
import com.example.pos.model.UpdatePelangganRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class PelangganRepository(private val supabase: SupabaseClient) {

    // Ambil List Pelanggan
    suspend fun fetchPelanggan(): List<Pelanggan> {
        return supabase.postgrest.rpc("get_pelanggan").decodeList<Pelanggan>()
    }

    suspend fun createPelanggan(request: CreatePelangganRequest) {
        supabase.postgrest.rpc("create_pelanggan", request)
    }

    suspend fun updatePelanggan(request: UpdatePelangganRequest) {
        supabase.postgrest.rpc("update_pelanggan", request)
    }
}