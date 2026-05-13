package com.example.pos.repository

import com.example.pos.data.SupabaseClientProvider
import com.example.pos.model.CreateProdukRequest
import com.example.pos.model.LogProduk
import com.example.pos.model.Produk
import com.example.pos.model.ToggleStatusProdukRequest
import com.example.pos.model.UpdateProdukRequest
import com.example.pos.model.UpdateStokManualRequest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class ProdukRepository {
    private val client = SupabaseClientProvider.client

    // ── READ ──────────────────────────────────────────────────────────────
    suspend fun getAllProduk(): List<Produk> =
        client.postgrest.rpc("get_all_produk").decodeList()

    suspend fun getProdukById(id: String): Produk =
        client.postgrest.rpc(
            "get_produk_by_id",
            mapOf("p_id" to id)
        ).decodeSingle()

    suspend fun getLogProduk(produkId: String): List<LogProduk> =
        client.postgrest.rpc(
            "get_log_produk",
            mapOf("p_produk_id" to produkId)
        ).decodeList()

    // ── CREATE ────────────────────────────────────────────────────────────
    suspend fun tambahProduk(nama: String, harga: Double, satuan: String, stok: Double) {
        client.postgrest.rpc(
            "create_produk",
            CreateProdukRequest(
                p_nama = nama,
                p_harga = harga,
                p_satuan = satuan,
                p_stok = stok
            )
        )
    }

    // ── UPDATE ────────────────────────────────────────────────────────────
    suspend fun updateProduk(id: String, nama: String, harga: Double, satuan: String) {
        client.postgrest.rpc(
            "update_produk",
            UpdateProdukRequest(
                p_id = id,
                p_nama = nama,
                p_harga = harga,
                p_satuan = satuan
            )
        )
    }

    suspend fun updateStokManual(produkId: String, tipe: String, qty: Int) {
        client.postgrest.rpc(
            "update_stok_manual",
            UpdateStokManualRequest(
                p_produk_id = produkId,
                p_tipe = tipe,
                p_qty = qty
            )
        )
    }

    suspend fun toggleStatusProduk(id: String, statusSaatIni: String) {
        client.postgrest.rpc(
            "toggle_status_produk",
            ToggleStatusProdukRequest(p_id = id)
        )
    }
}