package com.example.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PelangganSimple(
    val id: String = "",
    val nama: String = "",
    @SerialName("no_hp") val noHp: String? = null
)

@Serializable
data class KasPenjualan(
    val id: String = "",
    val nama: String = ""
)

@Serializable
data class ProdukSimple(
    val id: String = "",
    val nama: String = "",
    val satuan: String = "",
    val stok: Double = 0.0
)

@Serializable
data class PenjualanDetail(
    val id: String = "",
    @SerialName("produk_id") val produkId: String = "",
    val qty: Int = 0,
    @SerialName("harga_satuan") val hargaSatuan: Double = 0.0,
    val subtotal: Double = 0.0,
    val produk: ProdukSimple? = null
)

@Serializable
data class PenjualanWithRelasi(
    val id: String = "",
    @SerialName("kode_transaksi") val kodeTransaksi: String = "",
    val tanggal: String = "",
    @SerialName("pelanggan_id") val pelangganId: String = "",
    @SerialName("kas_id") val kasId: String = "",
    @SerialName("user_id") val userId: String = "",
    val total: Double = 0.0,
    val status: String = "draft",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String? = null,
    val pelanggan: PelangganSimple? = null,
    val kas: KasPenjualan? = null,
    val items: List<PenjualanDetail> = emptyList()
)

@Serializable
data class CreatePenjualanRequest(
    val p_pelanggan_id: String,
    val p_kas_id: String
)

@Serializable
data class AddItemPenjualanRequest(
    val p_penjualan_id: String,
    val p_produk_id: String,
    val p_qty: Int,
    val p_harga_satuan: Double
)

@Serializable
data class RemoveItemPenjualanRequest(
    val p_detail_id: String
)

@Serializable
data class PenjualanIdRequest(
    val p_penjualan_id: String
)

@Serializable
data class GetPenjualanDetailRequest(
    val p_penjualan_id: String
)

@Serializable
data class GetPenjualanListRequest(
    val p_is_admin: Boolean
)