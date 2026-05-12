package com.example.pos.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")

    // ── Produk ────────────────────────────────────────────────────────────
    object ProdukList : Screen("produk_list")

    object ProdukForm : Screen("produk_form") {
        // Mode tambah
        fun createRoute() = "produk_form"
        // Mode edit — kirim id lewat query param
        fun createEditRoute(id: String) = "produk_form?id=$id"
        // Route pattern untuk NavHost
        const val routeWithArgs = "produk_form?id={id}"
    }

    object ProdukDetail : Screen("produk_detail/{id}") {
        fun createRoute(id: String) = "produk_detail/$id"
    }

    // Kas
    object Kas : Screen("kas")

    // Main Screen
    object Main : Screen("main")

    // Home
    object Home : Screen("home")

    // Profile
    object Profile : Screen("profile")
}