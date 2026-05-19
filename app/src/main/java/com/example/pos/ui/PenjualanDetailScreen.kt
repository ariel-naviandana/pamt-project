package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pos.viewmodel.PenjualanUiState
import com.example.pos.viewmodel.PenjualanViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PenjualanDetailScreen(
    navController: NavController,
    penjualanId: String,
    isAdmin: Boolean,
    vm: PenjualanViewModel = viewModel()
) {
    val detailState by vm.detailState.collectAsStateWithLifecycle()
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var showBatalDialog by remember { mutableStateOf(false) }

    LaunchedEffect(penjualanId) { vm.loadDetail(penjualanId) }

    LaunchedEffect(uiState) {
        if (uiState is PenjualanUiState.Success) {
            vm.resetUiState()
            navController.popBackStack()
        }
    }

    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Transaksi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        when {
            detailState.isLoading -> {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }

            detailState.error != null -> {
                Box(Modifier.fillMaxSize()) {
                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Gagal memuat detail")
                        TextButton(onClick = { vm.loadDetail(penjualanId) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }

            detailState.penjualan != null -> {
                val p = detailState.penjualan!!

                LazyColumn(
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ── Info Header ───────────────────────────────────────
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    p.kodeTransaksi,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                HorizontalDivider()
                                InfoRowPenjualan("Pelanggan", p.pelanggan?.nama ?: "-")
                                InfoRowPenjualan("Kas", p.kas?.nama ?: "-")
                                InfoRowPenjualan("Tanggal", p.tanggal.take(10))
                                InfoRowPenjualan("Status", p.status)
                                HorizontalDivider()
                                InfoRowPenjualan(
                                    "Total",
                                    formatter.format(p.total),
                                    valueWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // ── Error ─────────────────────────────────────────────
                    if (uiState is PenjualanUiState.Error) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    (uiState as PenjualanUiState.Error).message,
                                    modifier = Modifier.padding(12.dp),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // ── Tombol Batalkan ───────────────────────────────────
                    if (p.status == "draft" && isAdmin) {
                        item {
                            OutlinedButton(
                                onClick = { showBatalDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                enabled = uiState !is PenjualanUiState.Loading
                            ) { Text("Batalkan Transaksi") }
                        }
                    }

                    // ── Header List Item ──────────────────────────────────
                    item {
                        Text(
                            "Item (${p.items.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // ── List Item ─────────────────────────────────────────
                    if (p.items.isEmpty()) {
                        item {
                            Text(
                                "Tidak ada item",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(items = p.items, key = { it.id }) { detail ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            detail.produk?.nama ?: "-",
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            "${detail.qty} ${detail.produk?.satuan ?: ""} × ${formatter.format(detail.hargaSatuan)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        formatter.format(detail.subtotal),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBatalDialog) {
        AlertDialog(
            onDismissRequest = { showBatalDialog = false },
            title = { Text("Batalkan Transaksi") },
            text = { Text("Yakin ingin membatalkan transaksi ini?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.batalkanPenjualan(penjualanId)
                    showBatalDialog = false
                }) { Text("Ya, Batalkan") }
            },
            dismissButton = {
                TextButton(onClick = { showBatalDialog = false }) { Text("Tidak") }
            }
        )
    }
}

@Composable
private fun InfoRowPenjualan(
    label: String,
    value: String,
    valueWeight: FontWeight = FontWeight.SemiBold
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = valueWeight)
    }
}