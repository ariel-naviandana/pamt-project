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
import androidx.compose.runtime.saveable.rememberSaveable
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

    // Mengubah remember menjadi rememberSaveable agar state aman saat rotasi layar
    var nama by rememberSaveable { mutableStateOf("") }
    var saldoInitial by rememberSaveable { mutableStateOf("") }

    // State untuk Penyesuaian Saldo (Hanya mode Edit)
    var adjustmentNominal by rememberSaveable { mutableStateOf("") }
    var adjustmentType by rememberSaveable { mutableStateOf("debit") }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }

    // GEMBOK PENANDA: Mengunci pengisian data awal agar tidak menimpa input baru saat rotasi layar
    var isInitialized by rememberSaveable { mutableStateOf(false) }

    // Load initial data untuk mode edit (Diproteksi oleh gembok isInitialized)
    LaunchedEffect(kasToEdit) {
        kasToEdit?.let {
            if (isEdit && !isInitialized) {
                nama = it.nama
                isInitialized = true // Kunci dinyalakan setelah data pertama kali dimuat
            }
        }
    }

    // Navigasi balik setelah sukses
    LaunchedEffect(uiState) {
        if (uiState is KasUiState.Success) {
            vm.resetUiState()
            navController.popBackStack()
        }
    }

    // Dialog Konfirmasi Hapus ditempatkan aman di level utama container
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

    // Mengganti Scaffold dengan Box sebagai container utama aplikasi agar bebas scroll saat landscape
    Box(modifier = Modifier.fillMaxSize()) {
        if (isEdit && kasToEdit == null && listState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (isEdit && kasToEdit == null && !listState.isLoading) {
            Text("Data kas tidak ditemukan", modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()), // Membuat seluruh halaman beserta header bisa di-scroll saat landscape
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // CUSTOM HEADER (Menggantikan TopAppBar bawaan Scaffold)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.offset(x = (-12).dp) // Meratakan posisi dengan form di bawahnya
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                    Text(
                        text = if (isEdit) "Edit Kas" else "Tambah Kas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    // Tombol delete ditaruh di pojok kanan header custom
                    if (isEdit && kasToEdit?.status != "nonaktif" && kasToEdit != null) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Nonaktifkan",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // ── SEKSI INFORMASI UMUM ──────────────────────────────────
                Text("Informasi Kas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Kas") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = uiState !is KasUiState.Loading
                )

                if (!isEdit) {
                    OutlinedTextField(
                        value = saldoInitial,
                        onValueChange = { saldoInitial = it },
                        label = { Text("Saldo Awal") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        enabled = uiState !is KasUiState.Loading
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
                        placeholder = { Text("0") },
                        enabled = uiState !is KasUiState.Loading
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = adjustmentType == "debit",
                            onClick = { if (uiState !is KasUiState.Loading) adjustmentType = "debit" },
                            label = { Text("Tambah (Debit)") },
                            modifier = Modifier.weight(1f),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = adjustmentType == "debit",
                                borderColor = MaterialTheme.colorScheme.outline,
                                selectedBorderColor = MaterialTheme.colorScheme.primary,
                                selectedBorderWidth = 2.dp
                            )
                        )
                        FilterChip(
                            selected = adjustmentType == "kredit",
                            onClick = { if (uiState !is KasUiState.Loading) adjustmentType = "kredit" },
                            label = { Text("Kurang (Kredit)") },
                            modifier = Modifier.weight(1f),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = adjustmentType == "kredit",
                                borderColor = MaterialTheme.colorScheme.outline,
                                selectedBorderColor = MaterialTheme.colorScheme.primary,
                                selectedBorderWidth = 2.dp
                            )
                        )
                    }
                }

                if (isEdit && kasToEdit?.status == "nonaktif") {
                    Button(
                        onClick = { vm.activateKas(kasToEdit.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        enabled = uiState !is KasUiState.Loading
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

                Spacer(modifier = Modifier.height(4.dp))

                // ── TOMBOL AKSI SUBMIT ────────────────────────────────────
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
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (isEdit) "Simpan Perubahan" else "Tambah Kas")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp)) // Jarak aman scroll paling bawah
            }
        }
    }
}