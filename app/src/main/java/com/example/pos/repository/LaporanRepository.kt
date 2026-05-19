package com.example.pos.repository

import com.example.pos.data.SupabaseClientProvider
import com.example.pos.model.LabaRugi
import io.github.jan.supabase.postgrest.postgrest

class LaporanRepository {
    private val client = SupabaseClientProvider.client

    suspend fun getLabaRugiAllTime(): LabaRugi {
        // Memanggil fungsi RPC get_laba_rugi yang mengembalikan single row object
        return client.postgrest.rpc("get_laba_rugi").decodeSingle<LabaRugi>()
    }
}