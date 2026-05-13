package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pos.model.KasSimple
import com.example.pos.viewmodel.PengeluaranUiState
import com.example.pos.viewmodel.PengeluaranViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengeluaranFormScreen(
    navController: NavController,
    pengeluaranId: String? = null,
    isAdmin: Boolean,
    vm: PengeluaranViewModel = viewModel()
) {
    val detailState by vm.detailState.collectAsStateWithLifecycle()
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val kasListState by vm.kasListState.collectAsStateWithLifecycle()

    val isEdit = pengeluaranId != null

    var deskripsi by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf("") }
    var selectedKas by remember { mutableStateOf<KasSimple?>(null) }
    var kasDropdownExpanded by remember { mutableStateOf(false) }

    // Validasi
    var deskripsiError by remember { mutableStateOf<String?>(null) }
    var nominalError by remember { mutableStateOf<String?>(null) }
    var kasError by remember { mutableStateOf<String?>(null) }

    // Load data jika mode edit
    LaunchedEffect(pengeluaranId) {
        if (pengeluaranId != null) vm.loadDetail(pengeluaranId)
        vm.loadKasAktif()
    }

    // Isi form saat mode edit
    LaunchedEffect(detailState.pengeluaran) {
        detailState.pengeluaran?.let { p ->
            if (isEdit) {
                deskripsi = p.deskripsi
                nominal = p.nominal.toInt().toString()
                selectedKas = p.kas
            }
        }
    }

    // Navigasi balik setelah sukses
    LaunchedEffect(uiState) {
        if (uiState is PengeluaranUiState.Success) {
            vm.resetUiState()
            navController.popBackStack()
        }
    }

    fun validate(): Boolean {
        var valid = true
        deskripsiError = if (deskripsi.isBlank()) { valid = false; "Deskripsi tidak boleh kosong" } else null
        nominalError = when {
            nominal.isBlank() -> { valid = false; "Nominal tidak boleh kosong" }
            nominal.toDoubleOrNull() == null -> { valid = false; "Nominal tidak valid" }
            nominal.toDouble() <= 0 -> { valid = false; "Nominal harus lebih dari 0" }
            else -> null
        }
        kasError = if (selectedKas == null) { valid = false; "Pilih kas terlebih dahulu" } else null
        return valid
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Edit Pengeluaran" else "Tambah Pengeluaran") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Deskripsi ─────────────────────────────────────────────────
            OutlinedTextField(
                value = deskripsi,
                onValueChange = { deskripsi = it; deskripsiError = null },
                label = { Text("Deskripsi / Keterangan") },
                isError = deskripsiError != null,
                supportingText = { if (deskripsiError != null) Text(deskripsiError!!) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // ── Nominal ───────────────────────────────────────────────────
            OutlinedTextField(
                value = nominal,
                onValueChange = { nominal = it; nominalError = null },
                label = { Text("Nominal (Rp)") },
                isError = nominalError != null,
                supportingText = { if (nominalError != null) Text(nominalError!!) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // ── Pilih Kas ─────────────────────────────────────────────────
            ExposedDropdownMenuBox(
                expanded = kasDropdownExpanded,
                onExpandedChange = { kasDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedKas?.nama ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Kas") },
                    isError = kasError != null,
                    supportingText = { if (kasError != null) Text(kasError!!) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = kasDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = kasDropdownExpanded,
                    onDismissRequest = { kasDropdownExpanded = false }
                ) {
                    if (kasListState.isLoading) {
                        DropdownMenuItem(
                            text = { Text("Memuat kas...") },
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
                                    kasError = null
                                    kasDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // ── Error server ──────────────────────────────────────────────
            if (uiState is PengeluaranUiState.Error) {
                Text(
                    text = (uiState as PengeluaranUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Submit ────────────────────────────────────────────────────
            Button(
                onClick = {
                    if (validate()) {
                        val n = nominal.toDouble()
                        val kasId = selectedKas!!.id
                        if (isEdit) vm.updatePengeluaran(pengeluaranId!!, kasId, deskripsi, n)
                        else vm.tambahPengeluaran(kasId, deskripsi, n)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is PengeluaranUiState.Loading
            ) {
                if (uiState is PengeluaranUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (isEdit) "Simpan Perubahan" else "Tambah Pengeluaran")
                }
            }
        }
    }
}