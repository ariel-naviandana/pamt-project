package com.example.pos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos.model.Profile
import com.example.pos.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    /*
     * Repository digunakan untuk mengakses Supabase.
     * Untuk materi dasar, repository dibuat langsung di ViewModel.
     *
     * Pada project besar, lebih baik gunakan Dependency Injection seperti Hilt.
     */
    private val repository = AuthRepository()

    /*
     * _uiState bersifat private agar hanya ViewModel yang bisa mengubah state.
     */
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)

    /*
     * uiState bersifat public agar UI hanya bisa membaca state,
     * tetapi tidak bisa mengubah langsung.
     */
    val uiState: StateFlow<AuthUiState> = _uiState

    private val _authCheckState = MutableStateFlow<AuthCheckState>(AuthCheckState.Checking)
    val authCheckState: StateFlow<AuthCheckState> = _authCheckState

    /*
     * State untuk input email.
     * Disimpan di ViewModel agar tetap aman saat recomposition.
     */
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    /*
     * State untuk input password.
     */
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    // State baru untuk menyimpan profil user (role)
    private val _userProfile = MutableStateFlow<Profile?>(null)
    val userProfile: StateFlow<Profile?> = _userProfile

    init {
        observeAuthStatus()
    }

    /*
     * Fungsi untuk memantau status autentikasi secara real-time.
     * Supabase akan otomatis memuat session dari storage saat app dibuka.
     */
    private fun observeAuthStatus() {
        viewModelScope.launch {
            repository.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        // Jika sudah login dari awal, ambil profilnya
                        _userProfile.value = repository.getUserProfile()
                        _authCheckState.value = AuthCheckState.Authenticated
                    }
                    is SessionStatus.NotAuthenticated -> {
                        _userProfile.value = null
                        _authCheckState.value = AuthCheckState.NotAuthenticated
                    }
                    is SessionStatus.Initializing -> {
                        _authCheckState.value = AuthCheckState.Checking
                    }
                    is SessionStatus.RefreshFailure -> {
                        if (repository.isLoggedIn()) {
                            _userProfile.value = repository.getUserProfile()
                            _authCheckState.value = AuthCheckState.Authenticated
                        } else {
                            _userProfile.value = null
                            _authCheckState.value = AuthCheckState.NotAuthenticated
                        }
                    }
                }
            }
        }
    }

    /*
     * Fungsi ini dipanggil dari UI ketika user mengetik email.
     */
    fun onEmailChange(value: String) {
        _email.value = value
    }

    /*
     * Fungsi ini dipanggil dari UI ketika user mengetik password.
     */
    fun onPasswordChange(value: String) {
        _password.value = value
    }

    /*
     * Fungsi login.
     * viewModelScope digunakan agar coroutine mengikuti lifecycle ViewModel.
     */
    fun login() {
        viewModelScope.launch {
            try {
                /*
                 * Ubah state menjadi Loading agar UI bisa menampilkan progress.
                 */
                _uiState.value = AuthUiState.Loading

                /*
                 * Panggil repository untuk login ke Supabase.
                 */
                repository.login(
                    email = _email.value,
                    password = _password.value
                )

                // Setelah berhasil login, ambil profilnya
                _userProfile.value = repository.getUserProfile()
                _uiState.value = AuthUiState.Success

            } catch (e: Exception) {
                /*
                 * Jika gagal, tampilkan pesan error.
                 */

                // Panggil fungsi helper untuk menyederhanakan pesan
                val friendlyMessage = mapSupabaseError(e.message ?: "Login gagal")
                _uiState.value = AuthUiState.Error(message = friendlyMessage)
            }
        }
    }

    /*
     * Fungsi register user baru.
     */
    fun register() {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading

                repository.register(
                    email = _email.value,
                    password = _password.value
                )

                _uiState.value = AuthUiState.Success

            } catch (e: Exception) {
                // Panggil fungsi helper untuk menyederhanakan pesan
                val friendlyMessage = mapSupabaseError(e.message ?: "Register gagal")
                _uiState.value = AuthUiState.Error(message = friendlyMessage)
            }
        }
    }

    /*
     * Fungsi logout.
     */
    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _userProfile.value = null // Hapus data profil saat logout
            _uiState.value = AuthUiState.Idle
        }
    }

    /*
     * Fungsi ini digunakan untuk mengembalikan state ke Idle.
     * Biasanya dipanggil setelah navigasi berhasil.
     */
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    private fun mapSupabaseError(rawMessage: String): String {
        return when {
            // Mendeteksi "Invalid credentials" (Email/Password salah)
            rawMessage.contains("credentials", ignoreCase = true) ->
                "Email atau password salah. Silakan cek kembali."

            // Mendeteksi error jaringan
            rawMessage.contains("network", ignoreCase = true) || rawMessage.contains("Unable to resolve host") ->
                "Gagal terhubung. Periksa koneksi internet Anda."

            // Mendeteksi password lemah
            rawMessage.contains("weak_password", ignoreCase = true) ->
                "Password lemah, minimal 6 karakter."

            // Mendeteksi format email tidak valid
            rawMessage.contains("invalid format", ignoreCase = true) ->
                "Format email tidak valid."

            // Jika tidak ada yang cocok, tampilkan pesan default yang sopan
            else -> "Terjadi kesalahan pada server. Coba lagi nanti."
        }
    }
}