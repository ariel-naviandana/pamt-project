package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    onLogoutClick: () -> Unit,
    onNavigateToProduk: () -> Unit,   // Tambah parameter ini
    onNavigateToKas: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Anda berhasil login ke aplikasi.")

        Spacer(modifier = Modifier.height(32.dp))

        // ── Menu Modul ────────────────────────────────────────────────────
        Button(
            onClick = onNavigateToProduk,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Manajemen Produk")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Nanti anggota lain tambah tombol modul mereka di sini
        // Button(onClick = onNavigateToKas) { Text("Manajemen Kas") }
        // Button(onClick = onNavigateToPelanggan) { Text("Manajemen Pelanggan") }
        // Button(onClick = onNavigateToPengeluaran) { Text("Pengeluaran") }

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol untuk pindah ke halaman Kas
        Button(
            onClick = onNavigateToKas,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Kelola Kas")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}