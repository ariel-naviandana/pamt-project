package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pos.viewmodel.PelangganViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPelangganScreen(
    navController: NavController,
    pelangganId: String? = null,
    viewModel: PelangganViewModel = viewModel()
) {
    var nama by remember { mutableStateOf("") }
    var noHp by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("aktif") }

    val snackbarHostState = remember { SnackbarHostState() }

    // Load data jika edit
    LaunchedEffect(pelangganId) {
        if (pelangganId != null) {
            // TODO: Implement load data dari repository berdasarkan ID
            // Untuk sementara, biarkan field kosong
        }
    }

    LaunchedEffect(viewModel.statusMessage) {
        viewModel.statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            if (it.contains("Berhasil")) {
                // Jika berhasil, kembali ke list
                navController.popBackStack()
            }
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(if (pelangganId == null) "Tambah Pelanggan" else "Edit Pelanggan")
                },
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ════════════════════════════════════════════════════════════════
            // INPUT NAMA PELANGGAN
            // ══════════════════════════════════════════════��═════════════════
            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
                label = { Text("Nama Pelanggan *") },
                modifier = Modifier.fillMaxWidth(),
                isError = nama.isEmpty()
            )

            // ════════════════════════════════════════════════════════════════
            // INPUT NO. TELP
            // ════════════════════════════════════════════════════════════════
            OutlinedTextField(
                value = noHp,
                onValueChange = { noHp = it },
                label = { Text("No. Telp *") },
                modifier = Modifier.fillMaxWidth(),
                isError = noHp.isEmpty()
            )

            // ════════════════════════════════════════════════════════════════
            // INPUT ALAMAT
            // ════════════════════════════════════════════════════════════════
            OutlinedTextField(
                value = alamat,
                onValueChange = { alamat = it },
                label = { Text("Alamat") },
                modifier = Modifier.fillMaxWidth()
            )

            // ════════════════════════════════════════════════════════════════
            // INPUT EMAIL
            // ════════════════════════════════════════════════════════════════
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            // ════════════════════════════════════════════════════════════════
            // STATUS (HANYA UNTUK EDIT)
            // ════════════════════════════════════════════════════════════════
            if (pelangganId != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Status Pelanggan Aktif")
                    Switch(
                        checked = status == "aktif",
                        onCheckedChange = { status = if (it) "aktif" else "nonaktif" }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ════════════════════════════════════════════════════════════════
            // BUTTON SIMPAN
            // ════════════════════════════════════════════════════════════════
            Button(
                onClick = {
                    viewModel.upsertPelanggan(
                        id = pelangganId,
                        nama = nama,
                        noHp = noHp,
                        alamat = alamat,
                        email = email,
                        status = status
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isLoading && nama.isNotEmpty() && noHp.isNotEmpty()
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("💾 Simpan")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ════════════════════════════════════════════════════════════════
            // BUTTON BATAL
            // ════════════════════════════════════════════════════════════════
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Batal")
            }
        }
    }
}