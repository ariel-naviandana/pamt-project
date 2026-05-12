package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pos.viewmodel.PelangganViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPelangganScreen(
    viewModel: PelangganViewModel,
    pelangganId: String? = null,
    initialNama: String = "",
    initialNoHp: String = "",
    initialAlamat: String = "",
    initialEmail: String = "",
    initialStatus: String = "aktif"
) {
    var nama by remember { mutableStateOf(initialNama) }
    var noHp by remember { mutableStateOf(initialNoHp) }
    var alamat by remember { mutableStateOf(initialAlamat) }
    var email by remember { mutableStateOf(initialEmail) }
    var status by remember { mutableStateOf(initialStatus) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.statusMessage) {
        viewModel.statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (pelangganId == null) "Tambah Pelanggan" else "Edit Pelanggan") }
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
            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
                label = { Text("Nama Pelanggan") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = noHp,
                onValueChange = { noHp = it },
                label = { Text("No. Telp") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = alamat,
                onValueChange = { alamat = it },
                label = { Text("Alamat") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

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

            Button(
                onClick = { viewModel.upsertPelanggan(pelangganId, nama, noHp, alamat, email, status) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isLoading && nama.isNotEmpty()
            ) {
                if (viewModel.isLoading) {
                    // PERBAIKAN DI SINI: Gunakan Modifier.size
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Simpan")
                }
            }
        }
    }
}