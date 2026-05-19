package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
    isAdmin: Boolean,
    viewModel: PelangganViewModel = viewModel()
) {
    val uiState by viewModel.formState.collectAsStateWithLifecycle()

    var nama by remember { mutableStateOf("") }
    var noHp by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("aktif") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(pelangganId) {
        if (pelangganId != null) {
            viewModel.loadPelangganById(pelangganId)
        }
    }

    LaunchedEffect(uiState.selectedPelanggan) {
        uiState.selectedPelanggan?.let { pelanggan ->
            nama = pelanggan.nama
            noHp = pelanggan.no_hp ?: ""
            alamat = pelanggan.alamat ?: ""
            email = pelanggan.email ?: ""
            status = pelanggan.status
        }
    }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            if (uiState.isSuccess) {
                navController.popBackStack()
            }
            viewModel.clearMessage()
        }
    }

    // PEROMBAKAN UTAMA: Mengganti Scaffold dengan Box sebagai container utama
    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading && uiState.selectedPelanggan == null && pelangganId != null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState), // Form bisa di-scroll dengan mulus
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                // HEADER PENGGANTI TOP-APP-BAR BUKAAN SCAFFOLD
                // Header ini akan ikut ter-scroll ke atas saat di landscape
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.offset(x = (-12).dp) // Geser sedikit agar rata kiri dengan form
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                    Text(
                        text = if (pelangganId == null) "Tambah Pelanggan" else "Edit Pelanggan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

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

                if (pelangganId != null && isAdmin) {
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

                // Hapus weight(1f), ganti dengan height statis agar tidak crash di dalam verticalScroll
                Spacer(modifier = Modifier.height(24.dp))

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

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    Text("Batal")
                }

                Spacer(modifier = Modifier.height(24.dp)) // Jarak ekstra di bagian paling bawah
            }
        }

        // Penempatan manual Snackbar di atas Box agar tetap muncul menggantikan fungsi bawaan Scaffold
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}