package com.example.pos.repository

import com.example.pos.data.SupabaseClientProvider
import com.example.pos.model.CreatePengeluaranRequest
import com.example.pos.model.KasSimple
import com.example.pos.model.PengeluaranIdRequest
import com.example.pos.model.PengeluaranWithKas
import com.example.pos.model.UpdatePengeluaranRequest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class PengeluaranRepository {
    private val client = SupabaseClientProvider.client

    // ── READ ──────────────────────────────────────────────────────────────
    suspend fun getPengeluaranList(isAdmin: Boolean): List<PengeluaranWithKas> =
        client.postgrest.rpc(
            "get_pengeluaran_list",
            mapOf("p_is_admin" to isAdmin)
        ).decodeList()

    suspend fun getPengeluaranById(id: String): PengeluaranWithKas =
        client.postgrest.rpc(
            "get_pengeluaran_by_id",
            mapOf("p_id" to id)
        ).decodeSingle()

    suspend fun getKasAktif(): List<KasSimple> =
        client.postgrest.rpc(
            "get_kas_list",
            mapOf("p_role" to "cashier") // cashier hanya lihat yang aktif
        ).decodeList()

    // ── CREATE ────────────────────────────────────────────────────────────
    suspend fun tambahPengeluaran(kasId: String, deskripsi: String, nominal: Double) {
        client.postgrest.rpc(
            "create_pengeluaran",
            CreatePengeluaranRequest(
                p_kas_id = kasId,
                p_deskripsi = deskripsi,
                p_nominal = nominal
            )
        )
    }

    // ── UPDATE ────────────────────────────────────────────────────────────
    suspend fun updatePengeluaran(id: String, kasId: String, deskripsi: String, nominal: Double) {
        client.postgrest.rpc(
            "update_pengeluaran",
            UpdatePengeluaranRequest(
                p_id = id,
                p_kas_id = kasId,
                p_deskripsi = deskripsi,
                p_nominal = nominal
            )
        )
    }

    suspend fun approvePengeluaran(id: String) {
        client.postgrest.rpc(
            "approve_pengeluaran",
            PengeluaranIdRequest(p_id = id)
        )
    }

    suspend fun batalkanPengeluaran(id: String) {
        client.postgrest.rpc(
            "batalkan_pengeluaran",
            PengeluaranIdRequest(p_id = id)
        )
    }
}