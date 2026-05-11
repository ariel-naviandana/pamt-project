package com.example.pos.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {

    /*
     * object digunakan agar Supabase client cukup dibuat satu kali.
     * Ini mirip singleton sederhana di Kotlin.
     */
    val client = createSupabaseClient(
        supabaseUrl = "https://zpznnyalnyycyazuozqx.supabase.co",
        supabaseKey = "sb_publishable_Eg3ke7eWZpDbOz-DHEq9IA_AUmQBmyq"
    ) {
        /*
         * install(Auth) digunakan agar aplikasi bisa memakai fitur autentikasi,
         * seperti login, register, logout, dan membaca session user.
         */
        install(Auth)
        install(Postgrest)
    }
}