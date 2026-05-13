package com.example.pos.repository

import com.example.pos.data.SupabaseClientProvider
import com.example.pos.model.CreateKasRequest
import com.example.pos.model.Kas
import com.example.pos.model.KasIdRequest
import com.example.pos.model.UpdateKasRequest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class KasRepository {
    private val supabase = SupabaseClientProvider.client

    suspend fun getKasList(role: String): List<Kas> =
        supabase.postgrest.rpc(
            "get_kas_list",
            mapOf("p_role" to role)
        ).decodeList()

    suspend fun insertKas(nama: String, saldo: Double) {
        supabase.postgrest.rpc(
            "create_kas",
            CreateKasRequest(p_nama = nama, p_saldo = saldo)
        )
    }

    suspend fun updateKas(id: String, nama: String, saldo: Double) {
        supabase.postgrest.rpc(
            "update_kas",
            UpdateKasRequest(p_id = id, p_nama = nama, p_saldo = saldo)
        )
    }

    suspend fun deleteKas(id: String) {
        supabase.postgrest.rpc(
            "delete_kas",
            KasIdRequest(p_id = id)
        )
    }

    suspend fun activateKas(id: String) {
        supabase.postgrest.rpc(
            "activate_kas",
            KasIdRequest(p_id = id)
        )
    }
}