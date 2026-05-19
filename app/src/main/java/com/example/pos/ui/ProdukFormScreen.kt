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
import com.example.pos.viewmodel.ProdukUiState
import com.example.pos.viewmodel.ProdukViewModel

@Composable
fun ProdukFormScreen(
    navController: NavController,
    produkId: String? = null,
    isAdmin: Boolean,
    vm: ProdukViewModel = viewModel()
) {
    val detailState by vm.detailState.collectAsStateWithLifecycle()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    val isEdit = produkId != null

    // Gunakan rememberSaveable untuk menyelamatkan input saat rotasi layar
    var nama by rememberSaveable { mutableStateOf("") }
    var harga by rememberSaveable { mutableStateOf("") }
    var satuan by rememberSaveable { mutableStateOf("") }
    var stok by rememberSaveable { mutableStateOf("") }

    // Validasi State
    var namaError by rememberSaveable { mutableStateOf<String?>(null) }
    var hargaError by rememberSaveable { mutableStateOf<String?>(null) }
    var satuanError by rememberSaveable { mutableStateOf<String?>(null) }
    var stokError by rememberSaveable { mutableStateOf<String?>(null) }

    // GEMBOK PENANDA: Agar data lama tidak menimpa ketikan baru pasca rotasi screen
    var isInitialized by rememberSaveable { mutableStateOf(false) }

    // Load data awal
    LaunchedEffect(produkId) {
        if (produkId != null && !isInitialized) {
            vm.loadDetail(produkId)
        }
    }

    // Isi form saat mode edit (Hanya dipicu sekali berkat filter gembok isInitialized)
    LaunchedEffect(detailState.produk) {
        detailState.produk?.let { p ->
            if (isEdit && !isInitialized) {
                nama = p.nama
                harga = p.harga.toInt().toString()
                satuan = p.satuan
                isInitialized = true // Kunci dinyalakan
            }
        }
    }

    // Navigasi balik setelah sukses
    LaunchedEffect(uiState) {
        if (uiState is ProdukUiState.Success) {
            vm.resetUiState()
            navController.popBackStack()
        }
    }

    fun validate(): Boolean {
        var valid = true

        if (nama.isBlank()) {
            valid = false; namaError = "Nama produk tidak boleh kosong"
        } else {
            namaError = null
        }

        val hargaDouble = harga.toDoubleOrNull()
        if (harga.isBlank()) {
            valid = false; hargaError = "Harga tidak boleh kosong"
        } else if (hargaDouble == null) {
            valid = false; hargaError = "Harga tidak valid"
        } else if (hargaDouble < 0) {
            valid = false; hargaError = "Harga tidak boleh minus"
        } else {
            hargaError = null
        }

        if (satuan.isBlank()) {
            valid = false; satuanError = "Satuan tidak boleh kosong"
        } else {
            satuanError = null
        }

        if (!isEdit) {
            val stokDouble = stok.toDoubleOrNull()
            if (stok.isBlank()) {
                valid = false; stokError = "Stok tidak boleh kosong"
            } else if (stokDouble == null) {
                valid = false; stokError = "Stok tidak valid"
            } else if (stokDouble < 0) {
                valid = false; stokError = "Stok tidak boleh minus"
            } else {
                stokError = null
            }
        }

        return valid
    }

    val scrollState = rememberScrollState()

    // Menggunakan Box + Column verticalScroll menggantikan Scaffold TopBar
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
                    text = if (isEdit) "Edit Produk" else "Tambah Produk",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // ── Nama ──────────────────────────────────────────────────────
            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it; namaError = null },
                label = { Text("Nama Produk") },
                isError = namaError != null,
                supportingText = { if (namaError != null) Text(namaError!!) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = uiState !is ProdukUiState.Loading
            )

            // ── Harga ─────────────────────────────────────────────────────
            OutlinedTextField(
                value = harga,
                onValueChange = { harga = it; hargaError = null },
                label = { Text("Harga (Rp)") },
                isError = hargaError != null,
                supportingText = { if (hargaError != null) Text(hargaError!!) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = uiState !is ProdukUiState.Loading
            )

            // ── Satuan ────────────────────────────────────────────────────
            OutlinedTextField(
                value = satuan,
                onValueChange = { satuan = it; satuanError = null },
                label = { Text("Satuan (pcs, kg, dll)") },
                isError = satuanError != null,
                supportingText = { if (satuanError != null) Text(satuanError!!) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = uiState !is ProdukUiState.Loading
            )

            // ── Stok Awal (hanya mode tambah) ─────────────────────────────
            if (!isEdit) {
                OutlinedTextField(
                    value = stok,
                    onValueChange = { stok = it; stokError = null },
                    label = { Text("Stok Awal") },
                    isError = stokError != null,
                    supportingText = { if (stokError != null) Text(stokError!!) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = uiState !is ProdukUiState.Loading
                )
            }

            // ── Error server ──────────────────────────────────────────────
            if (uiState is ProdukUiState.Error) {
                Text(
                    text = (uiState as ProdukUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Submit Button ─────────────────────────────────────────────
            Button(
                onClick = {
                    if (validate()) {
                        val h = harga.toDouble()
                        val s = stok.toDoubleOrNull() ?: 0.0
                        if (isEdit) vm.updateProduk(produkId!!, nama, h, satuan)
                        else vm.tambahProduk(nama, h, satuan, s)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is ProdukUiState.Loading
            ) {
                if (uiState is ProdukUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (isEdit) "Simpan Perubahan" else "Tambah Produk")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}