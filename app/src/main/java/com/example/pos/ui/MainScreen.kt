package com.example.pos.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userProfile: Profile?,
    onLogoutClick: () -> Unit
) {

    val navController = rememberNavController()
    val role = userProfile?.role ?: "cashier"
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Produk,
        BottomNavItem.Kas,
        BottomNavItem.Profile
    )

    val currentRoute =
        navController.currentBackStackEntryAsState()
            .value
            ?.destination
            ?.route

    val pageTitle = when {

        currentRoute == BottomNavItem.Home.route ->
            "POS Dashboard"

        currentRoute?.startsWith("produk") == true ->
            "Manajemen Produk"

        currentRoute == BottomNavItem.Kas.route ->
            "Manajemen Kas"

        currentRoute == BottomNavItem.Profile.route ->
            "Profile"

        else ->
            "POS Dashboard"
    }

    fun isSelected(itemRoute: String): Boolean {
        return when (itemRoute) {
            BottomNavItem.Produk.route -> {
                currentRoute?.startsWith("produk") == true
            }

            else -> {
                currentRoute == itemRoute
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(pageTitle)
                }
            )
        },
        bottomBar = {
            NavigationBar(

                containerColor =
                    MaterialTheme.colorScheme.surface
            ) {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = isSelected(item.route),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },

                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },

                        label = {
                            Text(item.title)
                        }
                    )
                }
            }
        },

    ) { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route
            ) {

                // ───────────────── HOME ─────────────────
                composable(BottomNavItem.Home.route) {

                    DashboardScreen(
                        profile = userProfile,

                        onNavigateToProduk = {
                            navController.navigate(
                                BottomNavItem.Produk.route
                            )
                        },

                        onNavigateToKas = {
                            navController.navigate(
                                BottomNavItem.Kas.route
                            )
                        }
                    )
                }

                // ───────────────── PRODUK LIST ─────────────────

                composable(BottomNavItem.Produk.route) {

                    ProdukListScreen(
                        navController = navController
                    )
                }

                // ───────────────── PRODUK DETAIL ─────────────────

                composable(
                    route = Screen.ProdukDetail.route,

                    arguments = listOf(
                        navArgument("id") {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->

                    val id =
                        backStackEntry.arguments?.getString("id")
                            ?: return@composable

                    ProdukDetailScreen(
                        navController = navController,
                        produkId = id
                    )
                }

                // ───────────────── PRODUK FORM ─────────────────
                composable(
                    route = Screen.ProdukForm.routeWithArgs,

                    arguments = listOf(
                        navArgument("id") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val id =
                        backStackEntry.arguments?.getString("id")

                    ProdukFormScreen(
                        navController = navController,
                        produkId = id
                    )
                }

                // ───────────────── KAS ─────────────────
                composable(BottomNavItem.Kas.route) {
                    val kasViewModel: KasViewModel = viewModel()

                    LaunchedEffect(role) {
                        kasViewModel.fetchKas(role)
                    }

                    KasScreen(
                        viewModel = kasViewModel,
                        userRole = role,
                        onBackClick = { }
                    )
                }

                // ───────────────── PROFILE ─────────────────
                composable(BottomNavItem.Profile.route) {
                    ProfileScreen(
                        profile = userProfile,
                        onLogoutClick = onLogoutClick
                    )
                }
            }
        }
    }
}