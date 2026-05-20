package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
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

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {

        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                // ── MENGGUNAKAN WARNA TEMA UNTUK CARD ADMIN ──
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
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

        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Menu Utama",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

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