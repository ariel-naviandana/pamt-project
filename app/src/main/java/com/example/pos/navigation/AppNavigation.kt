package com.example.pos.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pos.ui.DashboardScreen
import com.example.pos.ui.LoginScreen
import com.example.pos.ui.ProdukDetailScreen
import com.example.pos.ui.ProdukFormScreen
import com.example.pos.ui.ProdukListScreen
import com.example.pos.ui.RegisterScreen
import com.example.pos.viewmodel.AuthCheckState
import com.example.pos.viewmodel.AuthUiState
import com.example.pos.viewmodel.AuthViewModel
import com.example.pos.ui.KasScreen
import com.example.pos.viewmodel.KasViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val authCheckState = authViewModel.authCheckState.collectAsStateWithLifecycle()

    when (authCheckState.value) {
        is AuthCheckState.Checking -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is AuthCheckState.Authenticated -> {
            MainNavHost(
                authViewModel = authViewModel,
                startDestination = Screen.Dashboard.route
            )
        }

        is AuthCheckState.NotAuthenticated -> {
            MainNavHost(
                authViewModel = authViewModel,
                startDestination = Screen.Login.route
            )
        }
    }
}

@Composable
fun MainNavHost(
    authViewModel: AuthViewModel,
    startDestination: String
) {
    val navController = rememberNavController()

    // Ambil profile untuk mendapatkan role user
    val userProfile by authViewModel.userProfile.collectAsStateWithLifecycle()

    val email = authViewModel.email.collectAsStateWithLifecycle()
    val password = authViewModel.password.collectAsStateWithLifecycle()
    val uiState = authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.value) {
        if (uiState.value is AuthUiState.Success) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            authViewModel.resetState()
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ── Auth ──────────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                email = email.value,
                password = password.value,
                uiState = uiState.value,
                onEmailChange = authViewModel::onEmailChange,
                onPasswordChange = authViewModel::onPasswordChange,
                onLoginClick = { authViewModel.login() },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                email = email.value,
                password = password.value,
                uiState = uiState.value,
                onEmailChange = authViewModel::onEmailChange,
                onPasswordChange = authViewModel::onPasswordChange,
                onRegisterClick = { authViewModel.register() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToProduk = {
                    navController.navigate(Screen.ProdukList.route)
                },
                onNavigateToKas = {
                    navController.navigate(Screen.Kas.route)
                }
            )
        }

        // ── Produk ────────────────────────────────────────────────────────
        composable(Screen.ProdukList.route) {
            ProdukListScreen(navController = navController)
        }

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
            val id = backStackEntry.arguments?.getString("id")
            ProdukFormScreen(
                navController = navController,
                produkId = id
            )
        }

        composable(
            route = Screen.ProdukDetail.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            ProdukDetailScreen(
                navController = navController,
                produkId = id
            )
        }

        // ── Kas ────────────────────────────────────────────────────────────
        composable(Screen.Kas.route) {
            val kasViewModel: KasViewModel = viewModel()
            val role = userProfile?.role ?: "cashier"

            // Trigger fetch data saat layar dibuka
            LaunchedEffect(role) {
                kasViewModel.fetchKas(role)
            }

            KasScreen(
                viewModel = kasViewModel,
                userRole = role,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}