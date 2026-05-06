package com.example.pos.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth

object SupabaseClientProvider {

    /*
     * object digunakan agar Supabase client cukup dibuat satu kali.
     * Ini mirip singleton sederhana di Kotlin.
     */
    val client = createSupabaseClient(
        supabaseUrl = "https://jsigjqypwkjtkckjqarw.supabase.co",
        supabaseKey = "sb_publishable_SmbbMw_Lm8fd1rWxYKOrXw_07G4bTqr"
    ) {
        /*
         * install(Auth) digunakan agar aplikasi bisa memakai fitur autentikasi,
         * seperti login, register, logout, dan membaca session user.
         */
        install(Auth)
    }
}