package com.example.pos.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    object Home : BottomNavItem(
        "home",
        "Home",
        Icons.Default.Home
    )

    object Produk : BottomNavItem(
        "produk",
        "Produk",
        Icons.Default.Inventory2
    )

    object Kas : BottomNavItem(
        "kas",
        "Kas",
        Icons.Default.AccountBalanceWallet
    )

    object Profile : BottomNavItem(
        "profile",
        "Profil",
        Icons.Default.Person
    )
}