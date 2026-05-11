package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pos.model.Kas
import com.example.pos.viewmodel.KasUiState
import com.example.pos.viewmodel.KasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasScreen(
    viewModel: KasViewModel,
    userRole: String,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val selectedKas by viewModel.selectedKas.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manajemen Kas") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            // Jika role = admin, ada tombol untuk tambah kas
            if (userRole == "admin") {
                FloatingActionButton(
                    onClick = { viewModel.setShowAddDialog(true) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Kas")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                is KasUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is KasUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.fetchKas(userRole) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is KasUiState.Success -> {
                    if (state.data.isEmpty()) {
                        Text(
                            text = "Tidak ada data kas",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.data) { kas ->
                                KasItemCard(
                                    kas = kas,
                                    onClick = {
                                        if (userRole == "admin") viewModel.selectKas(kas)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddKasDialog(
                onDismiss = { viewModel.setShowAddDialog(false) },
                onConfirm = { nama, saldo ->
                    viewModel.addKas(nama, saldo)
                }
            )
        }

        selectedKas?.let { kas ->
            EditDeleteKasDialog(
                kas = kas,
                onDismiss = { viewModel.selectKas(null) },
                onConfirmUpdate = { nama, saldo ->
                    viewModel.updateKas(kas.id, nama, saldo)
                },
                onConfirmDelete = {
                    viewModel.deleteKas(kas.id)
                },
                onConfirmActivate = {
                    viewModel.activateKas(kas.id)
                }
            )
        }
    }
}

@Composable
fun AddKasDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var namaKas by remember { mutableStateOf("") }
    var saldoAwal by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Kas Baru") },
        text = {
            Column {
                OutlinedTextField(
                    value = namaKas,
                    onValueChange = { namaKas = it },
                    label = { Text("Nama Kas") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = saldoAwal,
                    onValueChange = { saldoAwal = it },
                    label = { Text("Saldo Awal") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(namaKas, saldoAwal) },
                enabled = namaKas.isNotBlank() && saldoAwal.isNotBlank()
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun EditDeleteKasDialog(
    kas: Kas,
    onDismiss: () -> Unit,
    onConfirmUpdate: (String, String) -> Unit,
    onConfirmDelete: () -> Unit,
    onConfirmActivate: () -> Unit
) {
    var namaKas by remember { mutableStateOf(kas.nama) }
    var saldoKas by remember { mutableStateOf(kas.saldo.toString()) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Nonaktifkan Kas") },
            text = { Text("Apakah Anda yakin ingin menonaktifkan '${kas.nama}'? Kasir tidak akan dapat melihat atau menggunakan kas ini.") },
            confirmButton = {
                Button(
                    onClick = onConfirmDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Nonaktifkan") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Batal") }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Edit Kas") },
            text = {
                Column {
                    OutlinedTextField(
                        value = namaKas,
                        onValueChange = { namaKas = it },
                        label = { Text("Nama Kas") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = saldoKas,
                        onValueChange = { saldoKas = it },
                        label = { Text("Saldo") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = { onConfirmUpdate(namaKas, saldoKas) }) { Text("Update") }
            },
            dismissButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (kas.status == "nonaktif") {
                        TextButton(
                            onClick = onConfirmActivate, // Gunakan callback parameter
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Aktifkan Kembali")
                        }
                    } else {
                        TextButton(
                            onClick = { showDeleteConfirm = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Nonaktifkan")
                        }
                    }

                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasItemCard(
    kas: Kas,
    onClick: () -> Unit
) {
    val isNonaktif = kas.status == "nonaktif"

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isNonaktif) Color.LightGray.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isNonaktif) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = kas.nama,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isNonaktif) Color.Gray else Color.Unspecified
                )

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (isNonaktif) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = kas.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isNonaktif) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Saldo Akhir",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = "Rp ${kas.saldo ?: 0}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = if (isNonaktif) Color.Gray else MaterialTheme.colorScheme.primary
            )
        }
    }
}