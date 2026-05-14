package com.example.pos.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")

    // ── Produk ──────────────────────────────────────────────────────────
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

    // ── Kas ───────────────────────────────────────────────────────────
    object KasList : Screen("kas_list")

    object KasForm : Screen("kas_form") {
        fun createRoute() = "kas_form"
        fun createEditRoute(id: String) = "kas_form?id=$id"
        const val routeWithArgs = "kas_form?id={id}"
    }

    // Main Screen
    object Main : Screen("main")

    // Home
    object Home : Screen("home")

    // Profile
    object Profile : Screen("profile")

    // ── Pengeluaran ───────────────────────────────────────────────────────
    object PengeluaranList : Screen("pengeluaran_list")

    object PengeluaranForm : Screen("pengeluaran_form") {
        fun createRoute() = "pengeluaran_form"
        fun createEditRoute(id: String) = "pengeluaran_form?id=$id"
        const val routeWithArgs = "pengeluaran_form?id={id}"
    }

    object PengeluaranDetail : Screen("pengeluaran_detail/{id}") {
        fun createRoute(id: String) = "pengeluaran_detail/$id"
    }

    // ── Pelanggan ──────────────────────────────────────────────────────
    object PelangganList : Screen("pelanggan_list")

    object PelangganForm : Screen("pelanggan_form") {
        fun createRoute() = "pelanggan_form"
        fun createEditRoute(id: String) = "pelanggan_form?id=$id"
        const val routeWithArgs = "pelanggan_form?id={id}"
    }

    // ── Penjualan ──────────────────────────────────────────────────────────
    object PenjualanList : Screen("penjualan_list")

    object PenjualanForm : Screen("penjualan_form") {
        fun createRoute() = "penjualan_form"
    }

    object PenjualanDetail : Screen("penjualan_detail/{id}") {
        fun createRoute(id: String) = "penjualan_detail/$id"
    }
}