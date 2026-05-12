package com.example.pos.repository

import com.example.pos.data.SupabaseClientProvider
import com.example.pos.model.LogProduk
import com.example.pos.model.LogProdukInsert
import com.example.pos.model.Produk
import com.example.pos.model.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class ProdukRepository {
    private val client = SupabaseClientProvider.client

    // ── PROFILE / ROLE ────────────────────────────────────────────────────
    suspend fun getUserRole(): String {
        val userId = client.auth.currentSessionOrNull()?.user?.id ?: return "cashier"
        val profile = client.from("profiles")
            .select { filter { eq("id", userId) } }
            .decodeSingle<UserProfile>()
        return profile.role
    }

    // ── READ ──────────────────────────────────────────────────────────────
    suspend fun getAllProduk(): List<Produk> =
        client.from("produk")
            .select()
            .decodeList()

    suspend fun getProdukById(id: String): Produk =
        client.from("produk")
            .select { filter { eq("id", id) } }
            .decodeSingle()

    suspend fun getLogProduk(produkId: String): List<LogProduk> =
        client.from("log_produk")
            .select {
                filter { eq("produk_id", produkId) }
                order("created_at", Order.DESCENDING)
            }
            .decodeList()

    // ── CREATE ────────────────────────────────────────────────────────────
    suspend fun tambahProduk(nama: String, harga: Double, satuan: String, stok: Double) {
        val produk = Produk(
            nama = nama,
            harga = harga,
            satuan = satuan,
            stok = stok
        )
        client.from("produk").insert(produk)
    }

    // ── UPDATE ────────────────────────────────────────────────────────────
    suspend fun updateProduk(id: String, nama: String, harga: Double, satuan: String) {
        client.from("produk").update(
            {
                set("nama", nama)
                set("harga", harga)
                set("satuan", satuan)
                set("updated_at", "now()")
            }
        ) { filter { eq("id", id) } }
    }

    suspend fun updateStokManual(produkId: String, tipe: String, qty: Int) {
        val produk = getProdukById(produkId)
        val stokBaru = if (tipe == "masuk") produk.stok + qty else produk.stok - qty
        require(stokBaru >= 0) { "Stok tidak cukup, stok saat ini: ${produk.stok.toInt()}" }

        val log = LogProdukInsert(
            produkId = produkId,
            refType = "manual",
            refId = produkId,
            tipe = tipe,
            qty = qty
        )

        client.from("log_produk").insert(log)
    }

    suspend fun toggleStatusProduk(id: String, statusSaatIni: String) {
        val statusBaru = if (statusSaatIni == "aktif") "nonaktif" else "aktif"
        client.from("produk").update(
            {
                set("status", statusBaru)
                set("updated_at", "now()")
            }
        ) { filter { eq("id", id) } }
    }
}