package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pos.viewmodel.LaporanUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LaporanScreen(
    uiState: LaporanUiState,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (uiState) {
            is LaporanUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is LaporanUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
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
            }
            is LaporanUiState.Success -> {
                val data = uiState.data
                val isLaba = data.labaRugi >= 0

                // Card Ringkasan Utama (Laba / Rugi Bersih)
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (isLaba) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isLaba) "LABA BERSIH" else "RUGI BERSIH",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isLaba) Color(0xFF2E7D32) else Color(0xFFC62828),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formatter.format(data.labaRugi),
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (isLaba) Color(0xFF2E7D32) else Color(0xFFC62828),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Detail Breakdown Card
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Rincian Keuangan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Baris Pendapatan Penjualan
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total Pendapatan (Penjualan)")
                            Text(
                                text = formatter.format(data.totalPenjualan),
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Baris Total Pengeluaran
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total Pengeluaran Toko")
                            Text(
                                text = formatter.format(data.totalPengeluaran),
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Akumulasi Akhir",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatter.format(data.labaRugi),
                                fontWeight = FontWeight.Bold,
                                color = if (isLaba) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Tombol Refresh Manual di bagian bawah halaman
                IconButton(
                    onClick = onRefreshClick,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Data",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}