package com.example.pos.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pos.model.Profile
import com.example.pos.navigation.BottomNavItem
import com.example.pos.navigation.Screen
import com.example.pos.viewmodel.KasViewModel
import com.example.pos.viewmodel.PelangganViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userProfile: Profile?,
    onLogoutClick: () -> Unit
) {
    val bottomNavController = rememberNavController()

    val role = userProfile?.role ?: "cashier"
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Produk,
        BottomNavItem.Kas,
        BottomNavItem.Pengeluaran,
        BottomNavItem.Pelanggan,
        BottomNavItem.Profile
    )

    val currentRoute = bottomNavController.currentBackStackEntryAsState().value?.destination?.route

    // Standarisasi Navigasi Tab (Agar Card dan Navbar sinkron)
    fun navigateToTab(route: String) {
        if (currentRoute == route) return

        bottomNavController.navigate(route) {
            // Menggunakan findStartDestination().id sesuai standar resmi Google
            popUpTo(bottomNavController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val pageTitle = when {
        currentRoute == BottomNavItem.Home.route -> "MyKasir Dashboard"
        currentRoute?.startsWith("produk") == true -> "Manajemen Produk"
        currentRoute?.startsWith("pengeluaran") == true -> "Manajemen Pengeluaran"
        currentRoute?.startsWith("pelanggan") == true -> "Manajemen Pelanggan"
        currentRoute?.startsWith("kas") == true -> "Manajemen Kas"
        currentRoute == BottomNavItem.Profile.route -> "Profile"
        else -> "MyKasir Dashboard"
    }

    // Agar ikon navbar tetap menyala saat masuk ke layar Detail/Form
    fun isSelected(itemRoute: String): Boolean {
        return when (itemRoute) {
            BottomNavItem.Home.route -> currentRoute == BottomNavItem.Home.route
            BottomNavItem.Produk.route -> currentRoute?.startsWith("produk") == true
            BottomNavItem.Pengeluaran.route -> currentRoute?.startsWith("pengeluaran") == true
            BottomNavItem.Pelanggan.route -> currentRoute?.startsWith("pelanggan") == true
            BottomNavItem.Kas.route -> currentRoute?.startsWith("kas") == true
            else -> currentRoute == itemRoute
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(pageTitle) })
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = isSelected(item.route),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        // Cukup panggil fungsi yang sudah distandarisasi
                        onClick = { navigateToTab(item.route) },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
//                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavItem.Home.route
            ) {

                // ── HOME / DASHBOARD ──
                composable(BottomNavItem.Home.route) {
                    DashboardScreen(
                        profile = userProfile,
                        onLogoutClick = onLogoutClick,
                        // Gunakan fungsi navigateToTab pada klik Card agar sama dengan Navbar
                        onNavigateToProduk = { navigateToTab(BottomNavItem.Produk.route) },
                        onNavigateToKas = { navigateToTab(BottomNavItem.Kas.route) },
                        onNavigateToPengeluaran = { navigateToTab(BottomNavItem.Pengeluaran.route) },
                        onNavigateToPelanggan = { navigateToTab(BottomNavItem.Pelanggan.route) }
                    )
                }

                // ── PRODUK ──
                composable(BottomNavItem.Produk.route) {
                    ProdukListScreen(navController = bottomNavController, isAdmin = role == "admin")
                }

                composable(
                    route = Screen.ProdukDetail.route,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: return@composable
                    ProdukDetailScreen(navController = bottomNavController, produkId = id, isAdmin = role == "admin")
                }

                composable(
                    route = Screen.ProdukForm.routeWithArgs,
                    arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true; defaultValue = null })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")
                    ProdukFormScreen(navController = bottomNavController, produkId = id, isAdmin = role == "admin")
                }

                // ── KAS ──────────────────────────────────────────────────────────
                composable(Screen.KasList.route) {
                    val kasViewModel: KasViewModel = viewModel()
                    KasListScreen(
                        navController = bottomNavController,
                        isAdmin = role == "admin",
                        vm = kasViewModel
                    )
                }

                composable(
                    route = Screen.KasForm.routeWithArgs,
                    arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true; defaultValue = null })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")
                    val kasViewModel: KasViewModel = viewModel()
                    KasFormScreen(
                        navController = bottomNavController,
                        kasId = id,
                        isAdmin = role == "admin",
                        vm = kasViewModel
                    )
                }

                // ── PROFILE ──
                composable(BottomNavItem.Profile.route) {
                    ProfileScreen(profile = userProfile, onLogoutClick = onLogoutClick)
                }

                // ── PENGELUARAN ──
                composable(BottomNavItem.Pengeluaran.route) {
                    PengeluaranListScreen(navController = bottomNavController, isAdmin = role == "admin")
                }

                composable(
                    route = Screen.PengeluaranForm.routeWithArgs,
                    arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true; defaultValue = null })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")
                    PengeluaranFormScreen(navController = bottomNavController, pengeluaranId = id, isAdmin = role == "admin")
                }

                composable(
                    route = Screen.PengeluaranDetail.route,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: return@composable
                    PengeluaranDetailScreen(navController = bottomNavController, pengeluaranId = id, isAdmin = role == "admin", currentUserId = userProfile?.id ?: "")
                }

                // ── PELANGGAN ──
                composable(BottomNavItem.Pelanggan.route) {
                    val pelangganViewModel: PelangganViewModel = viewModel()
                    PelangganListScreen(
                        viewModel = pelangganViewModel,
                        onAddPelanggan = { bottomNavController.navigate(Screen.PelangganForm.createRoute()) },
                        onEditPelanggan = { pelanggan -> bottomNavController.navigate(Screen.PelangganForm.createEditRoute(pelanggan.id)) }
                    )
                }

                composable(
                    route = Screen.PelangganForm.routeWithArgs,
                    arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true; defaultValue = null })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")
                    AddEditPelangganScreen(navController = bottomNavController, pelangganId = id)
                }
            }
        }
    }
}