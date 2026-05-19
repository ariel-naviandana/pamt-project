package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pos.model.KasSimple
import com.example.pos.model.Pelanggan
import com.example.pos.model.Produk
import com.example.pos.navigation.Screen
import com.example.pos.viewmodel.PenjualanUiState
import com.example.pos.viewmodel.PenjualanViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PenjualanFormScreen(
    navController: NavController,
    vm: PenjualanViewModel = viewModel()
) {
    val formState by vm.formState.collectAsStateWithLifecycle()
    val detailState by vm.detailState.collectAsStateWithLifecycle()
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val kasListState by vm.kasListState.collectAsStateWithLifecycle()
    val activeDraftId by vm.activeDraftId.collectAsStateWithLifecycle()

    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    var currentStep by remember { mutableStateOf(1) }
    var selectedPelanggan by remember { mutableStateOf<Pelanggan?>(null) }
    var selectedKas by remember { mutableStateOf<KasSimple?>(null) }
    var pelangganExpanded by remember { mutableStateOf(false) }
    var kasExpanded by remember { mutableStateOf(false) }
    var showTambahItemDialog by remember { mutableStateOf(false) }
    var showSelesaikanDialog by remember { mutableStateOf(false) }
    var showBatalDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.loadFormData()
        vm.resetDraft()
    }

    LaunchedEffect(activeDraftId) {
        if (activeDraftId != null) currentStep = 2
    }

    LaunchedEffect(uiState) {
        if (uiState is PenjualanUiState.Success) {
            vm.resetUiState()
            navController.navigate(Screen.PenjualanList.route) {
                popUpTo(Screen.PenjualanForm.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaksi Baru") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (activeDraftId != null) showBatalDialog = true
                        else navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // ── Step Indicator ────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Pelanggan & Kas", "Item", "Bayar").forEachIndexed { index, label ->
                    FilterChip(
                        selected = currentStep == index + 1,
                        onClick = {},
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider()

            when (currentStep) {

                // ── STEP 1: Pilih Pelanggan & Kas ─────────────────────────
                1 -> {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (formState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        } else {
                            // ── Dropdown Pelanggan ────────────────────────
                            ExposedDropdownMenuBox(
                                expanded = pelangganExpanded,
                                onExpandedChange = { pelangganExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = selectedPelanggan?.nama ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Pilih Pelanggan") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = pelangganExpanded
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = pelangganExpanded,
                                    onDismissRequest = { pelangganExpanded = false }
                                ) {
                                    if (formState.pelangganList.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text("Tidak ada pelanggan aktif") },
                                            onClick = {}
                                        )
                                    } else {
                                        formState.pelangganList.forEach { pelanggan ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(pelanggan.nama)
                                                        Text(
                                                            pelanggan.no_hp ?: "-",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    selectedPelanggan = pelanggan
                                                    pelangganExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // ── Dropdown Kas ──────────────────────────────
                            ExposedDropdownMenuBox(
                                expanded = kasExpanded,
                                onExpandedChange = { kasExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = selectedKas?.nama ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Pilih Kas") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = kasExpanded
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = kasExpanded,
                                    onDismissRequest = { kasExpanded = false }
                                ) {
                                    if (kasListState.isLoading) {
                                        DropdownMenuItem(
                                            text = { Text("Memuat kas...") },
                                            onClick = {}
                                        )
                                    } else if (kasListState.kasList.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text("Tidak ada kas aktif") },
                                            onClick = {}
                                        )
                                    } else {
                                        kasListState.kasList.forEach { kas ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(kas.nama)
                                                        Text(
                                                            "Saldo: Rp ${kas.saldo?.toLong() ?: 0}",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    selectedKas = kas
                                                    kasExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            if (uiState is PenjualanUiState.Error) {
                                Text(
                                    text = (uiState as PenjualanUiState.Error).message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Button(
                                onClick = {
                                    val pelangganId = selectedPelanggan?.id ?: return@Button
                                    val kasId = selectedKas?.id ?: return@Button
                                    vm.createDraft(pelangganId, kasId)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = selectedPelanggan != null
                                        && selectedKas != null
                                        && uiState !is PenjualanUiState.Loading
                            ) {
                                if (uiState is PenjualanUiState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text("Lanjut Pilih Item →")
                                }
                            }
                        }
                    }
                }

                // ── STEP 2: Tambah Item ────────────────────────────────────
                2 -> {
                    val penjualan = detailState.penjualan
                    Column(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (penjualan?.items.isNullOrEmpty()) {
                                item {
                                    Text(
                                        "Belum ada item, tambahkan produk",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                items(
                                    items = penjualan!!.items,
                                    key = { it.id }
                                ) { detail ->
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier
                                                .padding(12.dp)
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
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
                                                Text(
                                                    formatter.format(detail.subtotal),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            IconButton(onClick = { vm.removeItem(detail.id) }) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Hapus",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (uiState is PenjualanUiState.Error) {
                            Text(
                                text = (uiState as PenjualanUiState.Error).message,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        HorizontalDivider()

                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total", fontWeight = FontWeight.Bold)
                                Text(
                                    formatter.format(penjualan?.total ?: 0.0),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(
                                    onClick = { showTambahItemDialog = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Add, null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Tambah Item")
                                }
                                Button(
                                    onClick = { currentStep = 3 },
                                    modifier = Modifier.weight(1f),
                                    enabled = !penjualan?.items.isNullOrEmpty()
                                ) {
                                    Text("Lanjut Bayar →")
                                }
                            }
                        }
                    }
                }

                // ── STEP 3: Bayar ──────────────────────────────────────────
                3 -> {
                    val penjualan = detailState.penjualan
                    var jumlahBayar by remember { mutableStateOf("") }
                    val total = penjualan?.total ?: 0.0
                    val kembalian = (jumlahBayar.toDoubleOrNull() ?: 0.0) - total

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // ── Ringkasan ─────────────────────────────────────
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Ringkasan Transaksi", fontWeight = FontWeight.Bold)
                                HorizontalDivider()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Pelanggan")
                                    Text(penjualan?.pelanggan?.nama ?: "-")
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Kas")
                                    Text(penjualan?.kas?.nama ?: "-")
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Jumlah Item")
                                    Text("${penjualan?.items?.size ?: 0} item")
                                }
                                HorizontalDivider()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Total", fontWeight = FontWeight.Bold)
                                    Text(
                                        formatter.format(total),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }

                        // ── Input Bayar ───────────────────────────────────
                        OutlinedTextField(
                            value = jumlahBayar,
                            onValueChange = { jumlahBayar = it },
                            label = { Text("Jumlah Bayar") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            prefix = { Text("Rp ") }
                        )

                        // ── Kembalian ─────────────────────────────────────
                        if (jumlahBayar.isNotBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (kembalian >= 0)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        if (kembalian >= 0) "Kembalian" else "Kurang",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        formatter.format(
                                            if (kembalian >= 0) kembalian else -kembalian
                                        ),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (uiState is PenjualanUiState.Error) {
                            Text(
                                text = (uiState as PenjualanUiState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { showSelesaikanDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = kembalian >= 0
                                    && jumlahBayar.isNotBlank()
                                    && uiState !is PenjualanUiState.Loading
                        ) {
                            if (uiState is PenjualanUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Selesaikan Transaksi")
                            }
                        }

                        OutlinedButton(
                            onClick = { currentStep = 2 },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("← Kembali ke Item") }
                    }
                }
            }
        }
    }

    // ── Dialog Tambah Item ────────────────────────────────────────────────
    if (showTambahItemDialog) {
        TambahItemDialog(
            produkList = formState.produkList,
            onDismiss = { showTambahItemDialog = false },
            onConfirm = { produk, qty ->
                vm.addItem(produk.id, qty, produk.harga)
                showTambahItemDialog = false
            }
        )
    }

    // ── Dialog Selesaikan ─────────────────────────────────────────────────
    if (showSelesaikanDialog) {
        AlertDialog(
            onDismissRequest = { showSelesaikanDialog = false },
            title = { Text("Konfirmasi Transaksi") },
            text = {
                Text("Selesaikan transaksi ini? Stok produk akan berkurang dan saldo kas akan bertambah.")
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.selesaikanPenjualan()
                    showSelesaikanDialog = false
                }) { Text("Ya, Selesaikan") }
            },
            dismissButton = {
                TextButton(onClick = { showSelesaikanDialog = false }) { Text("Batal") }
            }
        )
    }

    // ── Dialog Batalkan Draft ─────────────────────────────────────────────
    if (showBatalDialog) {
        AlertDialog(
            onDismissRequest = { showBatalDialog = false },
            title = { Text("Batalkan Transaksi?") },
            text = { Text("Transaksi draft ini akan dibatalkan. Lanjutkan?") },
            confirmButton = {
                TextButton(onClick = {
                    activeDraftId?.let { vm.batalkanPenjualan(it) }
                    showBatalDialog = false
                    navController.popBackStack()
                }) { Text("Ya, Batalkan") }
            },
            dismissButton = {
                TextButton(onClick = { showBatalDialog = false }) { Text("Tidak") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TambahItemDialog(
    produkList: List<Produk>,
    onDismiss: () -> Unit,
    onConfirm: (Produk, Int) -> Unit
) {
    var selectedProduk by remember { mutableStateOf<Produk?>(null) }
    var qty by remember { mutableStateOf("1") }
    var produkExpanded by remember { mutableStateOf(false) }
    var qtyError by remember { mutableStateOf<String?>(null) }
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = produkExpanded,
                    onExpandedChange = { produkExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedProduk?.nama ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Produk") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = produkExpanded)
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = produkExpanded,
                        onDismissRequest = { produkExpanded = false }
                    ) {
                        if (produkList.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Tidak ada produk tersedia") },
                                onClick = {}
                            )
                        } else {
                            produkList.forEach { produk ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(produk.nama)
                                            Text(
                                                "Stok: ${produk.stok.toInt()} ${produk.satuan} • ${formatter.format(produk.harga)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedProduk = produk
                                        produkExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = qty,
                    onValueChange = { qty = it; qtyError = null },
                    label = { Text("Jumlah") },
                    isError = qtyError != null,
                    supportingText = { if (qtyError != null) Text(qtyError!!) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                selectedProduk?.let { produk ->
                    val q = qty.toIntOrNull() ?: 0
                    Text(
                        text = "Subtotal: ${formatter.format(produk.harga * q)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val produk = selectedProduk ?: return@TextButton
                val q = qty.toIntOrNull()
                when {
                    q == null || q <= 0 -> qtyError = "Jumlah harus lebih dari 0"
                    q > produk.stok.toInt() -> qtyError = "Melebihi stok tersedia (${produk.stok.toInt()})"
                    else -> onConfirm(produk, q)
                }
            }) { Text("Tambah") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}