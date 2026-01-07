package org.creategoodthings.vault.ui.pages.premium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.creategoodthings.vault.domain.Result
import org.creategoodthings.vault.domain.repositories.AuthRepository
import org.creategoodthings.vault.domain.services.PurchaseManager
import org.creategoodthings.vault.domain.services.SubscriptionOption

class LoginViewModel(
    private val _authRepo: AuthRepository,
    private val _purchaseManager: PurchaseManager
): ViewModel() {
    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState = _uiState.asStateFlow()

    private val _purchaseOptions = MutableStateFlow<PurchaseOptionsState>(PurchaseOptionsState.Loading)
    val purchaseOptions = _purchaseOptions.asStateFlow()

    init {
        viewModelScope.launch {
            _authRepo.currentUser.collect { user ->
                if (user != null) _uiState.update { it.copy(isSuccess = true, isLoading = false) }
                else _uiState.update { it.copy(isSuccess = false) }
            }
        }
        getPurchaseOptions()
    }

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

            result.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun logOut() {
        _uiState.value = LoginUIState()
        viewModelScope.launch {
            _authRepo.logout()
        }
    }

    private fun getPurchaseOptions() {
        viewModelScope.launch {
            _purchaseOptions.value = PurchaseOptionsState.Loading
            val result = _purchaseManager.getSubscriptionOptions()
            _purchaseOptions.value = when (result) {
                is Result.Success -> PurchaseOptionsState.Success(result.data)
                is Result.Error -> PurchaseOptionsState.Error(result.error.message)
            }
        }
    }

    fun purchaseSubscription(subscription: SubscriptionOption) {
        viewModelScope.launch {
            _purchaseManager.purchase(subscription)
        }
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

sealed interface PurchaseOptionsState {
    data object Loading : PurchaseOptionsState
    data class Success(val options: List<SubscriptionOption>) : PurchaseOptionsState
    data class Error(val message: String) : PurchaseOptionsState
}