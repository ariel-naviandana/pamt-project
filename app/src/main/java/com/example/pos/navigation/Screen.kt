package com.example.pos.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")

    // ── Produk ────────────────────────────────────────────────────────────
    object ProdukList : Screen("produk_list")

    object ProdukForm : Screen("produk_form") {
        fun createRoute() = "produk_form"
        fun createEditRoute(id: String) = "produk_form?id=$id"
        const val routeWithArgs = "produk_form?id={id}"
    }

    object ProdukDetail : Screen("produk_detail/{id}") {
        fun createRoute(id: String) = "produk_detail/$id"
    }

    // ── Kas ───────────────────────────────────────────────────────────────
    object Kas : Screen("kas")

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

    object PelangganList : Screen("pelanggan_list")
    object PelangganForm : Screen("pelanggan_form?id={id}") {
        val routeWithArgs = "pelanggan_form?id={id}"
    }
}