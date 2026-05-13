package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.pos.viewmodel.KasUiState
import com.example.pos.viewmodel.KasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasFormScreen(
    navController: NavController,
    kasId: String? = null,
    isAdmin: Boolean,
    vm: KasViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val listState by vm.listState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.init(isAdmin)
    }

    val isEdit = kasId != null
    val kasToEdit = remember(kasId, listState.kasList) {
        listState.kasList.find { it.id == kasId }
    }

    var nama by remember { mutableStateOf("") }
    var saldoInitial by remember { mutableStateOf("") }
    
    // State untuk Penyesuaian Saldo (Hanya mode Edit)
    var adjustmentNominal by remember { mutableStateOf("") }
    var adjustmentType by remember { mutableStateOf("debit") } // 'debit' = tambah, 'kredit' = kurang

    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Load initial data for edit
    LaunchedEffect(kasToEdit) {
        kasToEdit?.let {
            nama = it.nama
        }
    }

    // Success navigation
    LaunchedEffect(uiState) {
        if (uiState is KasUiState.Success) {
            vm.resetUiState()
            navController.popBackStack()
        }
    }

    if (showDeleteConfirm && kasToEdit != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Nonaktifkan Kas") },
            text = { Text("Apakah Anda yakin ingin menonaktifkan '${kasToEdit.nama}'? Kasir tidak akan dapat melihat atau menggunakan kas ini.") },
            confirmButton = {
                Button(
                    onClick = { 
                        vm.deleteKas(kasToEdit.id)
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Nonaktifkan") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Edit Kas" else "Tambah Kas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (isEdit && kasToEdit?.status != "nonaktif" && kasToEdit != null) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Nonaktifkan", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (isEdit && kasToEdit == null && listState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (isEdit && kasToEdit == null && !listState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Data kas tidak ditemukan")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── SEKSI INFORMASI UMUM ──────────────────────────────────
                Text("Informasi Kas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Kas") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (!isEdit) {
                    OutlinedTextField(
                        value = saldoInitial,
                        onValueChange = { saldoInitial = it },
                        label = { Text("Saldo Awal") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                } else {
                    // Tampilkan Saldo Saat Ini
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Saldo Saat Ini", style = MaterialTheme.typography.labelMedium)
                            Text(
                                "Rp ${kasToEdit?.saldo ?: 0}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // ── SEKSI PENYESUAIAN SALDO ─────────────────────────────
                    Text("Penyesuaian Saldo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "Masukkan nominal untuk menambah atau mengurangi saldo kas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = adjustmentNominal,
                        onValueChange = { adjustmentNominal = it },
                        label = { Text("Nominal Penyesuaian") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        placeholder = { Text("0") }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = adjustmentType == "debit",
                            onClick = { adjustmentType = "debit" },
                            label = { Text("Tambah (Debit)") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = adjustmentType == "kredit",
                            onClick = { adjustmentType = "kredit" },
                            label = { Text("Kurang (Kredit)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (isEdit && kasToEdit?.status == "nonaktif") {
                    Button(
                        onClick = { vm.activateKas(kasToEdit.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Aktifkan Kembali Kas Ini")
                    }
                }

                if (uiState is KasUiState.Error) {
                    Text(
                        text = (uiState as KasUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── TOMBOL AKSI ───────────────────────────────────────────
                Button(
                    onClick = {
                        if (isEdit && kasId != null) {
                            vm.updateKasAndAdjust(
                                id = kasId,
                                nama = nama,
                                currentSaldo = kasToEdit?.saldo ?: 0.0,
                                adjustmentType = adjustmentType,
                                adjustmentNominal = adjustmentNominal
                            )
                        } else {
                            vm.addKas(nama, saldoInitial)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is KasUiState.Loading && nama.isNotBlank() && (isEdit || saldoInitial.isNotBlank())
                ) {
                    if (uiState is KasUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(if (isEdit) "Simpan Perubahan" else "Tambah Kas")
                    }
                }
            }
        }
    }
}
