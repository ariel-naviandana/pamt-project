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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.pos.model.Pelanggan
import com.example.pos.viewmodel.PelangganViewModel
import com.example.pos.ui.theme.ActiveStatusBg
import com.example.pos.ui.theme.ActiveStatusText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PelangganListScreen(
    navController: NavController, // Ditambahkan agar bisa membaca savedStateHandle titipan dari form
    viewModel: PelangganViewModel,
    onAddPelanggan: () -> Unit,
    onEditPelanggan: (Pelanggan) -> Unit
) {
    val uiState by viewModel.listState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadPelanggan()
    }

    // Mengobservasi dan mengambil pesan titipan dari halaman form secara reaktif
    val pelangganMessage by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("pelanggan_message", null)
        ?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(null) }

    LaunchedEffect(pelangganMessage) {
        pelangganMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            // Segera hapus token setelah dibaca agar tidak memicu snackbar berulang kali saat rotasi layar
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("pelanggan_message")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, // Menaruh wadah Snackbar di sini
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPelanggan,
                modifier = Modifier.offset(y = 8.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->
        // Menggunakan modifier padding bawaan Scaffold agar list tidak menimpa area sistem atau FAB
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading && uiState.pelangganList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.errorMessage != null && uiState.pelangganList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.errorMessage ?: "Terjadi kesalahan")
                }
            } else if (uiState.pelangganList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Tidak ada data pelanggan",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 4.dp,
                        bottom = 120.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.pelangganList) { pelanggan ->
                        PelangganItem(
                            pelanggan = pelanggan,
                            onClick = { onEditPelanggan(pelanggan) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PelangganItem(
    pelanggan: Pelanggan,
    onClick: () -> Unit
) {
    val isNonaktif = pelanggan.status.lowercase() != "aktif"

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isNonaktif) 0.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = pelanggan.nama,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isNonaktif) Color.Gray else Color.Unspecified
                )

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (isNonaktif) MaterialTheme.colorScheme.errorContainer else ActiveStatusBg
                ) {
                    Text(
                        text = pelanggan.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isNonaktif) MaterialTheme.colorScheme.onErrorContainer else ActiveStatusText
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "No. Telp",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = pelanggan.no_hp ?: "-",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isNonaktif) Color.Gray else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}