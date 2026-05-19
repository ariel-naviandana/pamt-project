package com.example.pos.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pos.model.BulanFilter
import com.example.pos.viewmodel.LaporanUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LaporanScreen(
    uiState: LaporanUiState,
    listFilter: List<BulanFilter>,
    filterTerpilih: BulanFilter,
    onFilterSelected: (BulanFilter) -> Unit,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(if (isLandscape) 12.dp else 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── BARIS KONTROL (KONSISTEN DI ATAS UNTUK PORTRAIT & LANDSCAPE) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dropdown Menu Filter Bulan
            Box {
                OutlinedButton(onClick = { dropdownExpanded = true }) {
                    Text(text = filterTerpilih.label)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Pilih Bulan")
                }
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    listFilter.forEach { filter ->
                        DropdownMenuItem(
                            text = { Text(filter.label) },
                            onClick = {
                                onFilterSelected(filter)
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Tombol Refresh
            FilledTonalIconButton(onClick = onRefreshClick) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh Data")
            }
        }

        // ── KONTEN UTAMA ──
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is LaporanUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is LaporanUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onRefreshClick) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Coba Lagi")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Coba Lagi")
                        }
                    }
                }
                is LaporanUiState.Success -> {
                    val data = uiState.data
                    val isLaba = data.labaRugi >= 0

                    if (isLandscape) {
                        // ── LAYOUT LANDSCAPE (Kiri & Kanan) ──
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // KIRI: Card Laba/Rugi Bersih
                            ElevatedCard(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = if (isLaba) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (isLaba) "LABA BERSIH" else "RUGI BERSIH",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isLaba) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = formatter.format(data.labaRugi),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = if (isLaba) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            // KANAN: Detail Rincian
                            OutlinedCard(modifier = Modifier.weight(1.2f)) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = "Rincian Keuangan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "Total Pendapatan")
                                        Text(text = formatter.format(data.totalPenjualan), fontWeight = FontWeight.Medium)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "Total Pengeluaran")
                                        Text(text = formatter.format(data.totalPengeluaran), fontWeight = FontWeight.Medium)
                                    }
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "Akumulasi Akhir", fontWeight = FontWeight.Bold)
                                        Text(text = formatter.format(data.labaRugi), fontWeight = FontWeight.Bold, color = if (isLaba) Color(0xFF2E7D32) else Color(0xFFC62828))
                                    }
                                }
                            }
                        }
                    } else {
                        // ── LAYOUT PORTRAIT (Atas & Bawah) ──
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = if (isLaba) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                )
                            ) {
                                Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = if (isLaba) "LABA BERSIH" else "RUGI BERSIH", style = MaterialTheme.typography.labelLarge, color = if (isLaba) Color(0xFF2E7D32) else Color(0xFFC62828), fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = formatter.format(data.labaRugi), style = MaterialTheme.typography.headlineMedium, color = if (isLaba) Color(0xFF2E7D32) else Color(0xFFC62828), fontWeight = FontWeight.ExtraBold)
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(text = "Rincian Keuangan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(16.dp))

                                    // PERUBAHAN PORTRAIT: Menggunakan Column (New Line) agar teks tidak nabrak
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text(text = "Total Pendapatan (Penjualan)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = formatter.format(data.totalPenjualan), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text(text = "Total Pengeluaran Toko", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = formatter.format(data.totalPengeluaran), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "Akumulasi Akhir", fontWeight = FontWeight.Bold)
                                        Text(text = formatter.format(data.labaRugi), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = if (isLaba) Color(0xFF2E7D32) else Color(0xFFC62828))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}