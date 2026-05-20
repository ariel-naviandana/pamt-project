package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan // Pastikan ini ter-import
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pos.model.Profile
import com.example.pos.ui.components.DashboardMenuCard

@Composable
fun DashboardScreen(
    profile: Profile?,
    onNavigateToProduk: () -> Unit,
    onNavigateToKas: () -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateToPengeluaran: () -> Unit,
    onNavigateToPelanggan: () -> Unit,
    onNavigateToPenjualan: () -> Unit,
    onNavigateToLaporan: () -> Unit
) {
    val role = profile?.role ?: "cashier"

    // PEROMBAKAN UTAMA: Jadikan LazyVerticalGrid sebagai root (akar) layar.
    // Ini memastikan SELURUH isi halaman bisa di-scroll saat landscape.
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(20.dp), // Padding dipindah ke sini
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {

        // ── HEADER: Ucapan Selamat Datang ──
        // Menggunakan span agar card ini memakan lebar penuh (2 kolom grid)
        item(span = { GridItemSpan(maxLineSpan) }) {
            // Tiru gaya KasItemCard: Menggunakan Card biasa dengan elevation 2.dp
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Halo, ${profile?.nama ?: "User"}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Role: ${role.uppercase()}"
                    )
                }
            }
        }

        // ── JUDUL MENU ──
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Menu Utama",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        // ── ITEM MENU ──
        // Item di bawah ini akan otomatis dibagi menjadi 2 kolom
        item {
            DashboardMenuCard(
                title = "Produk",
                icon = Icons.Default.Inventory2,
                onClick = onNavigateToProduk
            )
        }

        item {
            DashboardMenuCard(
                title = "Pelanggan",
                icon = Icons.Default.People,
                onClick = onNavigateToPelanggan
            )
        }

        item {
            DashboardMenuCard(
                title = "Pengeluaran",
                icon = Icons.Default.Receipt,
                onClick = onNavigateToPengeluaran
            )
        }

        item {
            DashboardMenuCard(
                title = "Penjualan",
                icon = Icons.Default.ShoppingCart,
                onClick = onNavigateToPenjualan
            )
        }
    }
}