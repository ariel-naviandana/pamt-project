package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    // 1. Observasi Form UiState dari ViewModel
    val uiState by viewModel.formState.collectAsStateWithLifecycle()

    // STATE FORM LOKAL (Tetap dipertahankan agar pengetikan TextFields lancar)
    var nama by remember { mutableStateOf("") }
    var noHp by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("aktif") }

    val snackbarHostState = remember { SnackbarHostState() }

    // LOAD DATA UTK EDIT MODE
    LaunchedEffect(pelangganId) {
        if (pelangganId != null) {
            viewModel.loadPelangganById(pelangganId)
        }
    }

    // 2. SET VALUE FORM KETIKA DATA BERHASIL DI-LOAD DARI UISTATE
    LaunchedEffect(uiState.selectedPelanggan) {
        uiState.selectedPelanggan?.let { pelanggan ->
            nama = pelanggan.nama
            noHp = pelanggan.no_hp ?: ""
            alamat = pelanggan.alamat ?: ""
            email = pelanggan.email ?: ""
            status = pelanggan.status
        }
    }

    // 3. HANDLE NOTIFIKASI DAN BACK NAVIGATION MENGGUNAKAN UISTATE STATUS
    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            if (uiState.isSuccess) {
                kotlinx.coroutines.delay(1000)
                navController.popBackStack()
            }
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (pelangganId == null) "Tambah Pelanggan" else "Edit Pelanggan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        // 4. Kondisi Loading UI dari uiState
        if (uiState.isLoading && uiState.selectedPelanggan == null && pelangganId != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
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
                    label = { Text("Nama Pelanggan *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nama.isEmpty(),
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = noHp,
                    onValueChange = { noHp = it },
                    label = { Text("No. Telp *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = noHp.isEmpty(),
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = alamat,
                    onValueChange = { alamat = it },
                    label = { Text("Alamat") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
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
                            onCheckedChange = { status = if (it) "aktif" else "nonaktif" },
                            enabled = !uiState.isLoading
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

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
                    enabled = !uiState.isLoading && nama.isNotEmpty() && noHp.isNotEmpty()
                ) {
                    if (uiState.isLoading) {
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

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    Text("Batal")
                }
            }
        }
    }
}