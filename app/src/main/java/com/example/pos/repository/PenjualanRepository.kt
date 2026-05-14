package com.example.pos.repository

import com.example.pos.data.SupabaseClientProvider
import com.example.pos.model.AddItemPenjualanRequest
import com.example.pos.model.CreatePenjualanRequest
import com.example.pos.model.GetKasListRequest
import com.example.pos.model.GetPenjualanDetailRequest
import com.example.pos.model.GetPenjualanListRequest
import com.example.pos.model.KasSimple
import com.example.pos.model.Pelanggan
import com.example.pos.model.PenjualanIdRequest
import com.example.pos.model.PenjualanWithRelasi
import com.example.pos.model.Produk
import com.example.pos.model.RemoveItemPenjualanRequest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class PenjualanRepository {
    private val client = SupabaseClientProvider.client

    // ── READ ──────────────────────────────────────────────────────────────
    suspend fun getPenjualanList(isAdmin: Boolean): List<PenjualanWithRelasi> =
        client.postgrest.rpc(
            "get_penjualan_list",
            GetPenjualanListRequest(p_is_admin = isAdmin)
        ).decodeList()

    suspend fun getPenjualanDetail(penjualanId: String): PenjualanWithRelasi =
        client.postgrest.rpc(
            "get_penjualan_detail",
            GetPenjualanDetailRequest(p_penjualan_id = penjualanId)
        ).decodeSingle()

    suspend fun getPelangganAktif(): List<Pelanggan> =
        client.postgrest.rpc("get_pelanggan_aktif").decodeList()

    suspend fun getProdukAktif(): List<Produk> =
        client.postgrest.rpc("get_produk_aktif").decodeList()

    suspend fun getKasAktif(): List<KasSimple> =
        client.postgrest.rpc(
            "get_kas_list",
            GetKasListRequest(p_role = "cashier")
        ).decodeList()

    // ── CREATE ────────────────────────────────────────────────────────────
    suspend fun createPenjualan(pelangganId: String, kasId: String): String =
        client.postgrest.rpc(
            "create_penjualan",
            CreatePenjualanRequest(
                p_pelanggan_id = pelangganId,
                p_kas_id = kasId
            )
        ).decodeAs()

    suspend fun addItem(
        penjualanId: String,
        produkId: String,
        qty: Int,
        hargaSatuan: Double
    ) {
        client.postgrest.rpc(
            "add_item_penjualan",
            AddItemPenjualanRequest(
                p_penjualan_id = penjualanId,
                p_produk_id = produkId,
                p_qty = qty,
                p_harga_satuan = hargaSatuan
            )
        )
    }

    suspend fun removeItem(detailId: String) {
        client.postgrest.rpc(
            "remove_item_penjualan",
            RemoveItemPenjualanRequest(p_detail_id = detailId)
        )
    }

    // ── UPDATE STATUS ─────────────────────────────────────────────────────
    suspend fun selesaikanPenjualan(penjualanId: String) {
        client.postgrest.rpc(
            "selesaikan_penjualan",
            PenjualanIdRequest(p_penjualan_id = penjualanId)
        )
    }

    suspend fun batalkanPenjualan(penjualanId: String) {
        client.postgrest.rpc(
            "batalkan_penjualan",
            PenjualanIdRequest(p_penjualan_id = penjualanId)
        )
    }
}