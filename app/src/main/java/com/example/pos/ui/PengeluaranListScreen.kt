package com.example.pos.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pos.model.PengeluaranWithKas
import com.example.pos.navigation.Screen
import com.example.pos.viewmodel.PengeluaranViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengeluaranListScreen(
    navController: NavController,
    isAdmin: Boolean,
    vm: PengeluaranViewModel = viewModel()
) {
    val listState by vm.listState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { vm.init(isAdmin) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.PengeluaranForm.createRoute()) },
                modifier = Modifier.offset(y = 8.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Pengeluaran")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when {
                listState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                listState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Gagal memuat pengeluaran")
                        Text(
                            text = listState.error ?: "",  // tambah ini
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        TextButton(onClick = { vm.loadPengeluaran() }) {
                            Text("Coba Lagi")
                        }
                    }
                }

                listState.pengeluaranList.isEmpty() -> {
                    Text(
                        text = "Belum ada pengeluaran",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 4.dp,
                            bottom = 120.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = listState.pengeluaranList,
                            key = { it.id }
                        ) { pengeluaran ->
                            PengeluaranCard(
                                pengeluaran = pengeluaran,
                                onClick = {
                                    navController.navigate(
                                        Screen.PengeluaranDetail.createRoute(pengeluaran.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PengeluaranCard(
    pengeluaran: PengeluaranWithKas,
    onClick: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    val statusColor = when (pengeluaran.status) {
        "disetujui" -> MaterialTheme.colorScheme.primaryContainer
        "dibatalkan" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val statusTextColor = when (pengeluaran.status) {
        "disetujui" -> MaterialTheme.colorScheme.onPrimaryContainer
        "dibatalkan" -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pengeluaran.kodePengeluaran,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = pengeluaran.deskripsi,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatter.format(pengeluaran.nominal),
                    style = MaterialTheme.typography.bodyMedium
                )
                pengeluaran.kas?.let {
                    Text(
                        text = "Kas: ${it.nama}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = MaterialTheme.shapes.small,
                color = statusColor
            ) {
                Text(
                    text = pengeluaran.status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = statusTextColor
                )
            }
        }
    }
}