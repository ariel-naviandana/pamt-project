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
import com.example.pos.model.Produk
import com.example.pos.navigation.Screen
import com.example.pos.viewmodel.ProdukViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdukListScreen(
    navController: NavController,
    vm: ProdukViewModel = viewModel()
) {
    val listState by vm.listState.collectAsStateWithLifecycle()
    val userRole by vm.userRole.collectAsStateWithLifecycle()
    val isAdmin = userRole == "admin"

    LaunchedEffect(Unit) { vm.loadProduk() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Manajemen Produk") })
        },
        floatingActionButton = {
            // FAB hanya muncul untuk admin
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.ProdukForm.createRoute()) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
                }
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
                        Text(
                            text = "Gagal memuat produk",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        TextButton(onClick = { vm.loadProduk() }) {
                            Text("Coba Lagi")
                        }
                    }
                }

                listState.produkList.isEmpty() -> {
                    Text(
                        text = "Belum ada produk",
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
                            items = listState.produkList,
                            key = { it.id }
                        ) { produk ->
                            ProdukCard(
                                produk = produk,
                                onClick = {
                                    navController.navigate(
                                        Screen.ProdukDetail.createRoute(produk.id)
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
private fun ProdukCard(
    produk: Produk,
    onClick: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    text = produk.nama,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatter.format(produk.harga),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Stok: ${produk.stok.toInt()} ${produk.satuan}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (produk.stok <= 0)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Badge status
            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (produk.status == "aktif")
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = produk.status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (produk.status == "aktif")
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}