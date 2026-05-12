package com.example.pos.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pos.model.Pelanggan
import com.example.pos.viewmodel.PelangganViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PelangganListScreen(
    viewModel: PelangganViewModel,
    onAddPelanggan: () -> Unit,
    onEditPelanggan: (Pelanggan) -> Unit
) {
    // Load data saat screen dibuka
    LaunchedEffect(Unit) {
        viewModel.loadPelanggan()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Daftar Pelanggan") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPelanggan) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->
        if (viewModel.isLoading && viewModel.pelangganList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.pelangganList) { pelanggan ->
                    PelangganItem(pelanggan = pelanggan, onClick = { onEditPelanggan(pelanggan) })
                }
            }
        }
    }
}

@Composable
fun PelangganItem(pelanggan: Pelanggan, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = pelanggan.nama, style = MaterialTheme.typography.titleMedium)
            Text(text = "Telp: ${pelanggan.no_hp ?: "-"}", style = MaterialTheme.typography.bodySmall)
            Text(
                text = if (pelanggan.status == "aktif") "Status: Aktif" else "Status: Non-aktif",
                color = if (pelanggan.status == "aktif") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}