package com.example.pos.repository

import com.example.pos.data.SupabaseClientProvider
import com.example.pos.model.Kas
import com.example.pos.model.KasInsert
import com.example.pos.model.KasUpdate
import io.github.jan.supabase.postgrest.from

class KasRepository {
    private val supabase = SupabaseClientProvider.client

    // Mendapatkan daftar kas
    suspend fun getKasList(role: String): List<Kas> {
        return supabase.from("kas")
            .select {
                filter {
                    // Jika role = kasir, maka hanya tampil yang aktif
                    if (role == "cashier") {
                        eq("status", "aktif")
                    }
                }
            }
            .decodeList<Kas>()
    }

    // Tambah kas baru
    suspend fun insertKas(nama: String, saldo: Double) {
        val kasBaru = KasInsert(nama = nama, saldo = saldo)
        supabase.from("kas").insert(kasBaru)
    }

    // Update nama dan saldo kas
    suspend fun updateKas(id: String, nama: String, saldo: Double) {
        val dataUpdate = KasUpdate(
            nama = nama,
            saldo = saldo
        )

        supabase.from("kas").update(dataUpdate) {
            filter {
                eq("id", id)
            }
        }
    }

    // Soft delete kas
    suspend fun deleteKas(id: String) {
        // Menggunakan mapOf<String, String> aman dari error 'Any'
        val dataUpdate = mapOf("status" to "nonaktif")

        supabase.from("kas").update(dataUpdate) {
            filter { eq("id", id) }
        }
    }

    // Mengaktifkan kembali kas yang dinonaktifkan
    suspend fun activateKas(id: String) {
        val dataUpdate = mapOf("status" to "aktif")
        supabase.from("kas").update(dataUpdate) {
            filter { eq("id", id) }
        }
    }

}