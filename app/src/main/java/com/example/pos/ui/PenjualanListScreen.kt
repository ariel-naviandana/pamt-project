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
import com.example.pos.model.PenjualanWithRelasi
import com.example.pos.navigation.Screen
import com.example.pos.viewmodel.PenjualanViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PenjualanListScreen(
    navController: NavController,
    isAdmin: Boolean,
    vm: PenjualanViewModel = viewModel()
) {
    val listState by vm.listState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { vm.init(isAdmin) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.PenjualanForm.route) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Transaksi Baru")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
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
                        Text("Gagal memuat penjualan")
                        Text(
                            text = listState.error ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        TextButton(onClick = { vm.loadPenjualan() }) {
                            Text("Coba Lagi")
                        }
                    }
                }

                listState.penjualanList.isEmpty() -> {
                    Text(
                        text = "Belum ada transaksi",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = listState.penjualanList,
                            key = { it.id }
                        ) { penjualan ->
                            PenjualanCard(
                                penjualan = penjualan,
                                onClick = {
                                    navController.navigate(
                                        Screen.PenjualanDetail.createRoute(penjualan.id)
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
private fun PenjualanCard(
    penjualan: PenjualanWithRelasi,
    onClick: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    val statusColor = when (penjualan.status) {
        "selesai" -> MaterialTheme.colorScheme.primaryContainer
        "dibatalkan" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val statusTextColor = when (penjualan.status) {
        "selesai" -> MaterialTheme.colorScheme.onPrimaryContainer
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
                    text = penjualan.kodeTransaksi,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = penjualan.pelanggan?.nama ?: "-",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatter.format(penjualan.total),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Kas: ${penjualan.kas?.nama ?: "-"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = MaterialTheme.shapes.small,
                color = statusColor
            ) {
                Text(
                    text = penjualan.status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = statusTextColor
                )
            }
        }
    }
}