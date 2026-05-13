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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pos.ui.LoginScreen
import com.example.pos.ui.MainScreen
import com.example.pos.ui.RegisterScreen
import com.example.pos.viewmodel.AuthCheckState
import com.example.pos.viewmodel.AuthUiState
import com.example.pos.viewmodel.AuthViewModel

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
                startDestination = Screen.Main.route
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
    // NavController Utama (Root)
    val rootNavController = rememberNavController()

    val userProfile by authViewModel.userProfile.collectAsStateWithLifecycle()
    val email = authViewModel.email.collectAsStateWithLifecycle()
    val password = authViewModel.password.collectAsStateWithLifecycle()
    val uiState = authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.value) {
        if (uiState.value is AuthUiState.Success) {
            rootNavController.navigate(Screen.Main.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            authViewModel.resetState()
        }
    }

    NavHost(
        navController = rootNavController,
        startDestination = startDestination
    ) {
        // ── AUTH SECTION ──
        composable(Screen.Login.route) {
            LoginScreen(
                email = email.value,
                password = password.value,
                uiState = uiState.value,
                onEmailChange = authViewModel::onEmailChange,
                onPasswordChange = authViewModel::onPasswordChange,
                onLoginClick = { authViewModel.login() },
                onNavigateToRegister = { rootNavController.navigate(Screen.Register.route) }
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
                onNavigateToLogin = { rootNavController.popBackStack() }
            )
        }

        // ── MAIN SECTION (Membuka layar yang ada Bottom Nav-nya) ──
        composable(Screen.Main.route) {
            MainScreen(
                userProfile = userProfile,
                onLogoutClick = {
                    authViewModel.logout()
                    rootNavController.navigate(Screen.Login.route) {
                        popUpTo(0) // Bersihkan semua backstack saat logout
                    }
                }
            )
        }
    }
}