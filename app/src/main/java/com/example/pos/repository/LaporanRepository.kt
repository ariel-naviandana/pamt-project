package com.example.pos.repository

import com.example.pos.data.SupabaseClientProvider
import com.example.pos.model.BulanFilter
import com.example.pos.model.LabaRugi
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class LaporanRepository {
    private val client = SupabaseClientProvider.client

    // Fungsi untuk mengambil laporan laba rugi dengan filter tanggal (opsional)
    suspend fun getLabaRugi(startDate: String? = null, endDate: String? = null): LabaRugi {
        return client.postgrest.rpc(
            function = "get_laba_rugi",
            parameters = buildJsonObject {
                if (startDate != null) put("p_start_date", startDate)
                if (endDate != null) put("p_end_date", endDate)
            }
        ).decodeSingle<LabaRugi>()
    }

    // Fungsi untuk mengambil daftar bulan dari Supabase
    suspend fun getDaftarBulan(): List<BulanFilter> {
        return client.postgrest.rpc("get_daftar_bulan").decodeList<BulanFilter>()
    }
}