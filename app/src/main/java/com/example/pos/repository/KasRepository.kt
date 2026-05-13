package com.example.pos.repository

import com.example.pos.data.SupabaseClientProvider
import com.example.pos.model.CreateKasRequest
import com.example.pos.model.Kas
import com.example.pos.model.KasIdRequest
import com.example.pos.model.UpdateKasRequest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.Serializable

// Parameter RPC penyesuaian saldo
@Serializable
data class AdjustSaldoRequest(
    val p_kas_id: String,
    val p_tipe: String,
    val p_nominal: Double
)

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

    // RPC penyesuaian_saldo_kas
    suspend fun adjustSaldo(kasId: String, tipe: String, nominal: Double) {
        // Tipe: 'debit' (tambah) atau 'kredit' (kurang)
        supabase.postgrest.rpc(
            "penyesuaian_saldo_kas",
            AdjustSaldoRequest(
                p_kas_id = kasId,
                p_tipe = tipe,
                p_nominal = nominal
            )
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