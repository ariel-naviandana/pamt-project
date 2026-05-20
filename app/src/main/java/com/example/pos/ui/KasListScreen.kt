package com.example.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pos.model.Kas
import com.example.pos.navigation.Screen
import com.example.pos.ui.theme.ActiveStatusBg
import com.example.pos.ui.theme.ActiveStatusText
import com.example.pos.ui.theme.InactiveStatusBg
import com.example.pos.ui.theme.InactiveStatusText
import com.example.pos.viewmodel.KasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasListScreen(
    navController: NavController,
    isAdmin: Boolean,
    vm: KasViewModel = viewModel()
) {
    val listState by vm.listState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.init(isAdmin)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.KasForm.createRoute()) },
                    modifier = Modifier.offset(y = 8.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Kas")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()) {
            
            if (listState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (listState.error != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = listState.error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { vm.fetchKas() }) {
                        Text("Coba Lagi")
                    }
                }
            } else if (listState.kasList.isEmpty()) {
                Text(
                    text = "Tidak ada data kas",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 4.dp,
                        bottom = 120.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(listState.kasList) { kas ->
                        KasItemCard(
                            kas = kas,
                            onClick = {
                                if (isAdmin) {
                                    navController.navigate(Screen.KasForm.createEditRoute(kas.id))
                                }
                            }
                        )
                    }
                }
            }
        }
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
                    color = if (isNonaktif) InactiveStatusBg else ActiveStatusBg
                ) {
                    Text(
                        text = kas.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isNonaktif) InactiveStatusText else ActiveStatusText
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
