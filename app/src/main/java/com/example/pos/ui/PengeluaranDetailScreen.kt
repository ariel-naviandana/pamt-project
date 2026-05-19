package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
    currentUserId: String,
    vm: PengeluaranViewModel = viewModel()
) {
    val detailState by vm.detailState.collectAsStateWithLifecycle()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    // Menggunakan rememberSaveable agar status dialog aman saat rotasi layar
    var showApproveDialog by rememberSaveable { mutableStateOf(false) }
    var showBatalDialog by rememberSaveable { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    LaunchedEffect(pengeluaranId) {
        vm.loadDetail(pengeluaranId)
    }

    LaunchedEffect(uiState) {
        if (uiState is PengeluaranUiState.Success) vm.resetUiState()
    }

    // PEROMBAKAN UTAMA: Mengganti Scaffold dengan Box sebagai container utama
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            detailState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(scrollState), // Seluruh halaman detail bisa di-scroll dengan mulus
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // HEADER PENGGANTI TOP-APP-BAR BUKAAN SCAFFOLD
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.offset(x = (-12).dp) // Meratakan posisi ikon dengan konten
                        ) {
                            Icon(Icons.Default.ArrowBack, "Kembali")
                        }
                        Text(
                            text = "Detail Pengeluaran",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Tombol edit hanya jika status draft
                        if (p.status == "draft") {
                            val isOwner = p.userId == currentUserId
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

                    // Info Card
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

                    // Error aksi
                    if (uiState is PengeluaranUiState.Error) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = (uiState as PengeluaranUiState.Error).message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Tombol Aksi Admin
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

                    Spacer(modifier = Modifier.height(24.dp)) // Jarak aman paling bawah
                }
            }
        }

        // Dialog Approve placed safely at Box level
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

        // Dialog Batalkan placed safely at Box level
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