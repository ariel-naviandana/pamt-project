package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

    // PERUBAHAN UTAMA: Gunakan rememberSaveable untuk menyelamatkan input saat rotasi layar
    var deskripsi by rememberSaveable { mutableStateOf("") }
    var nominal by rememberSaveable { mutableStateOf("") }
    var kasDropdownExpanded by rememberSaveable { mutableStateOf(false) }

    var selectedKasId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedKasNama by rememberSaveable { mutableStateOf<String?>(null) }

    // Validasi State
    var deskripsiError by rememberSaveable { mutableStateOf<String?>(null) }
    var nominalError by rememberSaveable { mutableStateOf<String?>(null) }
    var kasError by rememberSaveable { mutableStateOf<String?>(null) }

    // GEMBOK PENANDA: Agar data lama tidak menimpa ketikan baru pasca rotasi screen
    var isInitialized by rememberSaveable { mutableStateOf(false) }

    // Load data awal
    LaunchedEffect(pengeluaranId) {
        if (pengeluaranId != null && !isInitialized) {
            vm.loadDetail(pengeluaranId)
        }
        vm.loadKasAktif()
    }

    // Isi form saat mode edit (Hanya dipicu sekali berkat filter gembok isInitialized)
    LaunchedEffect(detailState.pengeluaran) {
        detailState.pengeluaran?.let { p ->
            if (isEdit && !isInitialized) {
                deskripsi = p.deskripsi
                nominal = p.nominal.toInt().toString()
                selectedKasId = p.kas?.id
                selectedKasNama = p.kas?.nama
                isInitialized = true // Kunci dinyalakan!
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
        if (deskripsi.isBlank()) {
            valid = false
            deskripsiError = "Deskripsi tidak boleh kosong"
        } else {
            deskripsiError = null
        }

        val nominalDouble = nominal.toDoubleOrNull()
        if (nominal.isBlank()) {
            valid = false
            nominalError = "Nominal tidak boleh kosong"
        } else if (nominalDouble == null) {
            valid = false
            nominalError = "Nominal tidak valid"
        } else if (nominalDouble <= 0) {
            valid = false
            nominalError = "Nominal harus lebih dari 0"
        } else {
            nominalError = null
        }

        if (selectedKasId == null) {
            valid = false
            kasError = "Pilih kas terlebih dahulu"
        } else {
            kasError = null
        }
        return valid
    }

    val scrollState = rememberScrollState()

    // PEROMBAKAN UTAMA: Menggunakan Box + Column verticalScroll menggantikan Scaffold TopBar
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // HEADER FORM
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.offset(x = (-12).dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                }
                Text(
                    text = if (isEdit) "Edit Pengeluaran" else "Tambah Pengeluaran",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Deskripsi
            OutlinedTextField(
                value = deskripsi,
                onValueChange = { deskripsi = it; deskripsiError = null },
                label = { Text("Deskripsi / Keterangan") },
                isError = deskripsiError != null,
                supportingText = { if (deskripsiError != null) Text(deskripsiError!!) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                enabled = uiState !is PengeluaranUiState.Loading
            )

            // Nominal
            OutlinedTextField(
                value = nominal,
                onValueChange = { nominal = it; nominalError = null },
                label = { Text("Nominal (Rp)") },
                isError = nominalError != null,
                supportingText = { if (nominalError != null) Text(nominalError!!) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = uiState !is PengeluaranUiState.Loading
            )

            // Pilih Kas
            ExposedDropdownMenuBox(
                expanded = kasDropdownExpanded,
                onExpandedChange = {
                    if (uiState !is PengeluaranUiState.Loading) kasDropdownExpanded = it
                }
            ) {
                OutlinedTextField(
                    value = selectedKasNama?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Kas") },
                    isError = kasError != null,
                    supportingText = { if (kasError != null) Text(kasError!!) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = kasDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
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
                                            text = "Saldo: Rp ${kas.saldo?.toLong() ?: 0}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    selectedKasId = kas.id
                                    selectedKasNama = kas.nama
                                    kasError = null
                                    kasDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Error server
            if (uiState is PengeluaranUiState.Error) {
                Text(
                    text = (uiState as PengeluaranUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Submit Button
            Button(
                onClick = {
                    if (validate()) {
                        val n = nominal.toDouble()
                        val kasId = selectedKasId!!
                        if (isEdit) {
                            vm.updatePengeluaran(pengeluaranId!!, kasId, deskripsi, n)
                        } else {
                            vm.tambahPengeluaran(kasId, deskripsi, n)
                        }
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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}