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
import com.example.pos.viewmodel.ProdukUiState
import com.example.pos.viewmodel.ProdukViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdukFormScreen(
    navController: NavController,
    produkId: String? = null,
    vm: ProdukViewModel = viewModel()
) {
    val detailState by vm.detailState.collectAsStateWithLifecycle()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    val isEdit = produkId != null

    // State form lokal
    var nama by remember { mutableStateOf("") }
    var harga by remember { mutableStateOf("") }
    var satuan by remember { mutableStateOf("") }
    var stok by remember { mutableStateOf("") }

    // Validasi
    var namaError by remember { mutableStateOf<String?>(null) }
    var hargaError by remember { mutableStateOf<String?>(null) }
    var satuanError by remember { mutableStateOf<String?>(null) }
    var stokError by remember { mutableStateOf<String?>(null) }

    // Load data jika mode edit
    LaunchedEffect(produkId) {
        if (produkId != null) vm.loadDetail(produkId)
    }

    // Isi form saat data produk berhasil dimuat (mode edit)
    LaunchedEffect(detailState.produk) {
        detailState.produk?.let { p ->
            if (isEdit) {
                nama = p.nama
                harga = p.harga.toInt().toString()
                satuan = p.satuan
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

        namaError = if (nama.isBlank()) {
            valid = false; "Nama produk tidak boleh kosong"
        } else null

        hargaError = if (harga.isBlank()) {
            valid = false; "Harga tidak boleh kosong"
        } else if (harga.toDoubleOrNull() == null || harga.toDouble() < 0) {
            valid = false; "Harga tidak valid"
        } else null

        satuanError = if (satuan.isBlank()) {
            valid = false; "Satuan tidak boleh kosong"
        } else null

        if (!isEdit) {
            stokError = if (stok.isBlank()) {
                valid = false; "Stok tidak boleh kosong"
            } else if (stok.toDoubleOrNull() == null || stok.toDouble() < 0) {
                valid = false; "Stok tidak valid"
            } else null
        }

        return valid
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Edit Produk" else "Tambah Produk") },
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
            // ── Nama ──────────────────────────────────────────────────────
            OutlinedTextField(
                value = nama,
                onValueChange = {
                    nama = it
                    namaError = null
                },
                label = { Text("Nama Produk") },
                isError = namaError != null,
                supportingText = {
                    if (namaError != null) Text(namaError!!)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // ── Harga ─────────────────────────────────────────────────────
            OutlinedTextField(
                value = harga,
                onValueChange = {
                    harga = it
                    hargaError = null
                },
                label = { Text("Harga (Rp)") },
                isError = hargaError != null,
                supportingText = {
                    if (hargaError != null) Text(hargaError!!)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // ── Satuan ────────────────────────────────────────────────────
            OutlinedTextField(
                value = satuan,
                onValueChange = {
                    satuan = it
                    satuanError = null
                },
                label = { Text("Satuan (pcs, kg, lusin, dll)") },
                isError = satuanError != null,
                supportingText = {
                    if (satuanError != null) Text(satuanError!!)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // ── Stok Awal (hanya mode tambah) ─────────────────────────────
            if (!isEdit) {
                OutlinedTextField(
                    value = stok,
                    onValueChange = {
                        stok = it
                        stokError = null
                    },
                    label = { Text("Stok Awal") },
                    isError = stokError != null,
                    supportingText = {
                        if (stokError != null) Text(stokError!!)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // ── Error dari server ─────────────────────────────────────────
            if (uiState is ProdukUiState.Error) {
                Text(
                    text = (uiState as ProdukUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Tombol Submit ─────────────────────────────────────────────
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
        }
    }
}