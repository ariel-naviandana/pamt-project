package com.example.pos.viewmodel

import com.example.pos.model.LabaRugi

sealed interface LaporanUiState {
    object Loading : LaporanUiState
    data class Success(val data: LabaRugi) : LaporanUiState
    data class Error(val message: String) : LaporanUiState
}