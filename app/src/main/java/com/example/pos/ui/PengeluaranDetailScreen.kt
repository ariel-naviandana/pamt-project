package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pos.navigation.Screen
import com.example.pos.viewmodel.PengeluaranUiState
import com.example.pos.viewmodel.PengeluaranViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengeluaranDetailScreen(
    navController: NavController,
    pengeluaranId: String,
    isAdmin: Boolean,
    vm: PengeluaranViewModel = viewModel()
) {
    val detailState by vm.detailState.collectAsStateWithLifecycle()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    var showApproveDialog by remember { mutableStateOf(false) }
    var showBatalDialog by remember { mutableStateOf(false) }

    LaunchedEffect(pengeluaranId) { vm.loadDetail(pengeluaranId) }

    LaunchedEffect(uiState) {
        if (uiState is PengeluaranUiState.Success) vm.resetUiState()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pengeluaran") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    // Tombol edit hanya jika status draft
                    val pengeluaran = detailState.pengeluaran
                    if (pengeluaran != null && pengeluaran.status == "draft") {
                        val isOwner = true // RLS sudah handle di backend
                        if (isAdmin || isOwner) {
                            IconButton(onClick = {
                                navController.navigate(
                                    Screen.PengeluaranForm.createEditRoute(pengeluaranId)
                                )
                            }) {
                                Icon(Icons.Default.Edit, "Edit")
                            }
                        }
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
                        TextButton(onClick = { vm.loadDetail(pengeluaranId) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }

            detailState.pengeluaran != null -> {
                val p = detailState.pengeluaran!!
                val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ── Info Card ─────────────────────────────────────────
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = p.kodePengeluaran,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            HorizontalDivider()
                            InfoRowPengeluaran(label = "Deskripsi", value = p.deskripsi)
                            InfoRowPengeluaran(label = "Nominal", value = formatter.format(p.nominal))
                            InfoRowPengeluaran(label = "Kas", value = p.kas?.nama ?: "-")
                            InfoRowPengeluaran(label = "Tanggal", value = p.tanggal.take(10))
                            InfoRowPengeluaran(label = "Status", value = p.status)
                        }
                    }

                    // ── Error aksi ────────────────────────────────────────
                    if (uiState is PengeluaranUiState.Error) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = (uiState as PengeluaranUiState.Error).message,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // ── Tombol Aksi Admin ─────────────────────────────────
                    if (isAdmin && p.status == "draft") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showApproveDialog = true },
                                modifier = Modifier.weight(1f),
                                enabled = uiState !is PengeluaranUiState.Loading
                            ) {
                                Text("Setujui")
                            }

                            OutlinedButton(
                                onClick = { showBatalDialog = true },
                                modifier = Modifier.weight(1f),
                                enabled = uiState !is PengeluaranUiState.Loading,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Batalkan")
                            }
                        }
                    }

                    // Cashier hanya bisa batalkan milik sendiri yang masih draft
                    if (!isAdmin && p.status == "draft") {
                        OutlinedButton(
                            onClick = { showBatalDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = uiState !is PengeluaranUiState.Loading,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Batalkan")
                        }
                    }
                }
            }
        }
    }

    // ── Dialog Approve ────────────────────────────────────────────────────
    if (showApproveDialog) {
        AlertDialog(
            onDismissRequest = { showApproveDialog = false },
            title = { Text("Setujui Pengeluaran") },
            text = { Text("Pengeluaran akan diproses dan saldo kas akan berkurang. Lanjutkan?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.approvePengeluaran(pengeluaranId)
                    showApproveDialog = false
                }) { Text("Ya, Setujui") }
            },
            dismissButton = {
                TextButton(onClick = { showApproveDialog = false }) { Text("Batal") }
            }
        )
    }

    // ── Dialog Batalkan ───────────────────────────────────────────────────
    if (showBatalDialog) {
        AlertDialog(
            onDismissRequest = { showBatalDialog = false },
            title = { Text("Batalkan Pengeluaran") },
            text = { Text("Yakin ingin membatalkan pengeluaran ini?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.batalkanPengeluaran(pengeluaranId)
                    showBatalDialog = false
                }) { Text("Ya, Batalkan") }
            },
            dismissButton = {
                TextButton(onClick = { showBatalDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun InfoRowPengeluaran(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}