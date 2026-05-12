package com.example.pos.repository

import com.example.pos.data.SupabaseClientProvider
import com.example.pos.model.Kas
import com.example.pos.model.PengeluaranInsert
import com.example.pos.model.PengeluaranWithKas
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class PengeluaranRepository {
    private val client = SupabaseClientProvider.client

    // ── USER ──────────────────────────────────────────────────────────────
    fun getCurrentUserId(): String? =
        client.auth.currentSessionOrNull()?.user?.id

    // ── READ ──────────────────────────────────────────────────────────────
    suspend fun getPengeluaranList(isAdmin: Boolean): List<PengeluaranWithKas> {
        val userId = getCurrentUserId()
        return client.from("pengeluaran")
            .select(
                columns = io.github.jan.supabase.postgrest.query.Columns.raw("*, kas(id, nama, saldo, status)")
            ) {
                filter {
                    if (!isAdmin && userId != null) {
                        eq("user_id", userId)
                    }
                }
                order("created_at", Order.DESCENDING)
            }
            .decodeList()
    }

    suspend fun getPengeluaranById(id: String): PengeluaranWithKas =
        client.from("pengeluaran")
            .select(
                columns = io.github.jan.supabase.postgrest.query.Columns.raw("*, kas(id, nama, saldo, status)")
            ) {
                filter { eq("id", id) }
            }
            .decodeSingle()

    suspend fun getKasAktif(): List<Kas> =
        client.from("kas")
            .select {
                filter { eq("status", "aktif") }
            }
            .decodeList()

    // ── CREATE ────────────────────────────────────────────────────────────
    suspend fun tambahPengeluaran(
        kasId: String,
        deskripsi: String,
        nominal: Double
    ) {
        val userId = getCurrentUserId() ?: throw Exception("User tidak ditemukan")
        val kode = generateKode()

        val data = PengeluaranInsert(
            kodePengeluaran = kode,
            kasId = kasId,
            userId = userId,
            deskripsi = deskripsi,
            nominal = nominal
        )
        client.from("pengeluaran").insert(data)
    }

    // ── UPDATE ────────────────────────────────────────────────────────────
    suspend fun updatePengeluaran(id: String, kasId: String, deskripsi: String, nominal: Double) {
        client.from("pengeluaran").update(
            {
                set("kas_id", kasId)
                set("deskripsi", deskripsi)
                set("nominal", nominal)
                set("updated_at", "now()")
            }
        ) { filter { eq("id", id) } }
    }

    suspend fun approvePengeluaran(id: String) {
        client.from("pengeluaran").update(
            {
                set("status", "disetujui")
                set("updated_at", "now()")
            }
        ) { filter { eq("id", id) } }
        // Trigger handle_pengeluaran_kas otomatis kurangi saldo kas
    }

    suspend fun batalkanPengeluaran(id: String) {
        client.from("pengeluaran").update(
            {
                set("status", "dibatalkan")
                set("updated_at", "now()")
            }
        ) { filter { eq("id", id) } }
    }

    // ── HELPER ────────────────────────────────────────────────────────────
    private fun generateKode(): String {
        val timestamp = System.currentTimeMillis()
        return "OUT-$timestamp"
    }
}