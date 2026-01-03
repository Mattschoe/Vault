package org.creategoodthings.vault.ui.pages.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.creategoodthings.vault.domain.repositories.AuthRepository

class LoginViewModel(
    private val _authRepo: AuthRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState = _uiState.asStateFlow()

    fun onUsernameChange(newUserName: String) { _uiState.update { it.copy(username = newUserName, error = null) }}
    fun onEmailChange(newEmail: String) { _uiState.update { it.copy(email = newEmail, error = null) }}
    fun onPasswordChange(newPassword: String) { _uiState.update { it.copy(password = newPassword, error = null) } }
    fun toggleMode() { _uiState.update { it.copy(isRegisterMode = !it.isRegisterMode, error = null) }}
    fun submit() {
        val currentState = _uiState.value
        if (currentState.isLoading) return
        if (currentState.isRegisterMode && currentState.username.isBlank()) {
            _uiState.update { it.copy(error = "Username is required") } //TODO find en måde at gøre det her non english only
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = if (currentState.isRegisterMode) {
                _authRepo.register(
                    username = currentState.username,
                    email = currentState.email,
                    password = currentState.password
                )
            } else {
                _authRepo.login(
                    email = currentState.email,
                    password =currentState.password
                )
            }

            result.fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, isSuccess = true) }},
                onFailure = { error -> _uiState.update { it.copy(isLoading = false, error = error.message) }}
            )
        }
    }

    fun reset() {
        _uiState.value = LoginUIState()
    }
}

data class LoginUIState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isRegisterMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)