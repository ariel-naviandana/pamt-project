package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.pos.model.LogProduk
import com.example.pos.model.Produk
import com.example.pos.navigation.Screen
import com.example.pos.viewmodel.ProdukUiState
import com.example.pos.viewmodel.ProdukViewModel
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdukDetailScreen(
    navController: NavController,
    produkId: String,
    isAdmin: Boolean,
    vm: ProdukViewModel = viewModel()
) {
    val detailState by vm.detailState.collectAsStateWithLifecycle()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    var showStokDialog by remember { mutableStateOf(false) }
    var showToggleDialog by remember { mutableStateOf(false) }

    LaunchedEffect(produkId) { vm.loadDetail(produkId) }

    // Reset uiState setelah aksi berhasil
    LaunchedEffect(uiState) {
        if (uiState is ProdukUiState.Success) vm.resetUiState()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Produk") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    // Tombol edit hanya untuk admin
                    if (isAdmin) {
                        IconButton(onClick = {
                            navController.navigate(
                                Screen.ProdukForm.createEditRoute(produkId)
                            )
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Produk")
                        }
                    }
                }
            )
        }
    ) { padding ->

        when {
            detailState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            detailState.error != null -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Gagal memuat detail produk")
                        TextButton(onClick = { vm.loadDetail(produkId) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }

            detailState.produk != null -> {
                val produk = detailState.produk!!
                val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // ── Info Produk ───────────────────────────────────────
                    item {
                        InfoProdukCard(produk = produk, formatter = formatter)
                    }

                    // ── Snackbar error aksi ───────────────────────────────
                    if (uiState is ProdukUiState.Error) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = (uiState as ProdukUiState.Error).message,
                                    modifier = Modifier.padding(12.dp),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // ── Tombol Aksi (hanya admin) ─────────────────────────
                    if (isAdmin) {
                        item {
                            AdminAksiRow(
                                produk = produk,
                                isLoading = uiState is ProdukUiState.Loading,
                                onUbahStok = { showStokDialog = true },
                                onToggleStatus = { showToggleDialog = true }
                            )
                        }
                    }

                    // ── Header Log ────────────────────────────────────────
                    item {
                        Text(
                            text = "Histori Stok",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // ── Log Items ─────────────────────────────────────────
                    if (detailState.logList.isEmpty()) {
                        item {
                            Text(
                                text = "Belum ada histori stok",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        items(
                            items = detailState.logList,
                            key = { it.id }
                        ) { log ->
                            LogProdukCard(log = log)
                        }
                    }
                }
            }
        }
    }

    // ── Dialog Ubah Stok ──────────────────────────────────────────────────
    if (showStokDialog) {
        StokDialog(
            onDismiss = { showStokDialog = false },
            onConfirm = { tipe, qty ->
                vm.updateStokManual(produkId, tipe, qty)
                showStokDialog = false
            }
        )
    }

    // ── Dialog Toggle Status ──────────────────────────────────────────────
    if (showToggleDialog) {
        val produk = detailState.produk
        if (produk != null) {
            AlertDialog(
                onDismissRequest = { showToggleDialog = false },
                title = { Text("Konfirmasi") },
                text = {
                    Text(
                        "Yakin ingin ${
                            if (produk.status == "aktif") "menonaktifkan" else "mengaktifkan"
                        } produk \"${produk.nama}\"?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            vm.toggleStatusProduk(produkId, produk.status)
                            showToggleDialog = false
                        }
                    ) { Text("Ya") }
                },
                dismissButton = {
                    TextButton(onClick = { showToggleDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

// ── Composable Helpers ────────────────────────────────────────────────────────

@Composable
private fun InfoProdukCard(
    produk: Produk,
    formatter: NumberFormat
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = produk.nama,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
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
                ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
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
    var tipe by remember { mutableStateOf("masuk") }
    var qty by remember { mutableStateOf("") }
    var qtyError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah Stok Manual") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // ── Pilih Tipe ────────────────────────────────────────────
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = tipe == "masuk",
                        onClick = { tipe = "masuk" }
                    )
                    Text("Stok Masuk")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = tipe == "keluar",
                        onClick = { tipe = "keluar" }
                    )
                    Text("Stok Keluar")
                }

                // ── Input Jumlah ──────────────────────────────────────────
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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
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