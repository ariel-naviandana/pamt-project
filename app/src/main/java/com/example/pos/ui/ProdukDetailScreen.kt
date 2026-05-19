package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pos.model.LogProduk
import com.example.pos.model.Produk
import com.example.pos.navigation.Screen
import com.example.pos.viewmodel.ProdukUiState
import com.example.pos.viewmodel.ProdukViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProdukDetailScreen(
    navController: NavController,
    produkId: String,
    isAdmin: Boolean,
    vm: ProdukViewModel = viewModel()
) {
    val detailState by vm.detailState.collectAsStateWithLifecycle()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    // Menggunakan rememberSaveable agar status dialog aman saat rotasi layar
    var showStokDialog by rememberSaveable { mutableStateOf(false) }
    var showToggleDialog by rememberSaveable { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    LaunchedEffect(produkId) {
        vm.loadDetail(produkId)
    }

    LaunchedEffect(uiState) {
        if (uiState is ProdukUiState.Success) vm.resetUiState()
    }

    // Mengganti Scaffold dengan Box sebagai container utama
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
                        TextButton(onClick = { vm.loadDetail(produkId) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }

            detailState.produk != null -> {
                val p = detailState.produk!!
                val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(scrollState), // Seluruh halaman detail bisa di-scroll dengan mulus
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // HEADER PENGGANTI TOP-APP-BAR
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
                            text = "Detail Produk",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        // Tombol edit hanya untuk admin
                        if (isAdmin) {
                            IconButton(onClick = {
                                navController.navigate(Screen.ProdukForm.createEditRoute(produkId))
                            }) {
                                Icon(Icons.Default.Edit, "Edit")
                            }
                        }
                    }

                    // ── Info Produk ───────────────────────────────────────
                    InfoProdukCard(produk = p, formatter = formatter)

                    // ── Snackbar error aksi ───────────────────────────────
                    if (uiState is ProdukUiState.Error) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = (uiState as ProdukUiState.Error).message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // ── Tombol Aksi (hanya admin) ─────────────────────────
                    if (isAdmin) {
                        AdminAksiRow(
                            produk = p,
                            isLoading = uiState is ProdukUiState.Loading,
                            onUbahStok = { showStokDialog = true },
                            onToggleStatus = { showToggleDialog = true }
                        )
                    }

                    // ── Header Log ────────────────────────────────────────
                    Text(
                        text = "Histori Stok",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // ── Log Items ─────────────────────────────────────────
                    if (detailState.logList.isEmpty()) {
                        Text(
                            text = "Belum ada histori stok",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        // Menggunakan forEach karena kita berada di dalam Column yang sudah scrollable
                        detailState.logList.forEach { log ->
                            LogProdukCard(log = log)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp)) // Jarak aman paling bawah
                }
            }
        }

        // Dialog Ubah Stok placed safely at Box level
        if (showStokDialog) {
            StokDialog(
                onDismiss = { showStokDialog = false },
                onConfirm = { tipe, qty ->
                    vm.updateStokManual(produkId, tipe, qty)
                    showStokDialog = false
                }
            )
        }

        // Dialog Toggle Status placed safely at Box level
        if (showToggleDialog) {
            val p = detailState.produk
            if (p != null) {
                AlertDialog(
                    onDismissRequest = { showToggleDialog = false },
                    title = { Text("Konfirmasi") },
                    text = {
                        Text(
                            "Yakin ingin ${
                                if (p.status == "aktif") "menonaktifkan" else "mengaktifkan"
                            } produk \"${p.nama}\"?"
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                vm.toggleStatusProduk(produkId, p.status)
                                showToggleDialog = false
                            }
                        ) { Text("Ya") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showToggleDialog = false }) { Text("Batal") }
                    }
                )
            }
        }
    }
}

// ── Composable Helpers ────────────────────────────────────────────────────────

@Composable
private fun InfoProdukCard(produk: Produk, formatter: NumberFormat) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = produk.nama,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider()
            InfoRow(label = "Harga", value = formatter.format(produk.harga))
            InfoRow(label = "Satuan", value = produk.satuan)
            InfoRow(
                label = "Stok",
                value = "${produk.stok.toInt()} ${produk.satuan}",
                valueColor = if (produk.stok <= 0)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurface
            )
            InfoRow(label = "Status", value = produk.status)
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
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
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@Composable
private fun AdminAksiRow(
    produk: Produk,
    isLoading: Boolean,
    onUbahStok: () -> Unit,
    onToggleStatus: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onUbahStok,
            modifier = Modifier.weight(1f),
            enabled = !isLoading
        ) {
            Text("Ubah Stok")
        }

        OutlinedButton(
            onClick = onToggleStatus,
            modifier = Modifier.weight(1f),
            enabled = !isLoading,
            colors = if (produk.status == "aktif")
                ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            else
                ButtonDefaults.outlinedButtonColors()
        ) {
            Text(if (produk.status == "aktif") "Nonaktifkan" else "Aktifkan")
        }
    }
}

@Composable
private fun LogProdukCard(log: LogProduk) {
    val isMasuk = log.tipe == "masuk"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "${if (isMasuk) "+" else "-"}${log.qty}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isMasuk)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
                Text(
                    text = "via ${log.refType}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = log.createdAt.take(10),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StokDialog(
    onDismiss: () -> Unit,
    onConfirm: (tipe: String, qty: Int) -> Unit
) {
    var tipe by rememberSaveable { mutableStateOf("masuk") }
    var qty by rememberSaveable { mutableStateOf("") }
    var qtyError by rememberSaveable { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah Stok Manual") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = tipe == "masuk", onClick = { tipe = "masuk" })
                    Text("Stok Masuk")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = tipe == "keluar", onClick = { tipe = "keluar" })
                    Text("Stok Keluar")
                }

                OutlinedTextField(
                    value = qty,
                    onValueChange = {
                        qty = it
                        qtyError = null
                    },
                    label = { Text("Jumlah") },
                    isError = qtyError != null,
                    supportingText = { if (qtyError != null) Text(qtyError!!) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val q = qty.toIntOrNull()
                    when {
                        q == null -> qtyError = "Masukkan angka yang valid"
                        q <= 0 -> qtyError = "Jumlah harus lebih dari 0"
                        else -> onConfirm(tipe, q)
                    }
                }
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}