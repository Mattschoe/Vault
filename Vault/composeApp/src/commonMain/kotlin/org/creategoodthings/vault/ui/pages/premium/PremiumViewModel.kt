package org.creategoodthings.vault.ui.pages.premium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.creategoodthings.vault.domain.Result
import org.creategoodthings.vault.domain.Storage
import org.creategoodthings.vault.domain.repositories.AuthRepository
import org.creategoodthings.vault.domain.repositories.ProductRepository
import org.creategoodthings.vault.domain.services.PurchaseManager
import org.creategoodthings.vault.domain.services.SubscriptionOption
import org.jetbrains.compose.resources.StringResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.no_email_inputted
import vault.composeapp.generated.resources.no_storage_chosen

class LoginViewModel(
    private val _productRepo: ProductRepository,
    private val _authRepo: AuthRepository,
    private val _purchaseManager: PurchaseManager
): ViewModel() {
    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState = _uiState.asStateFlow()

    private val _purchaseOptions = MutableStateFlow<PurchaseOptionsState>(PurchaseOptionsState.Loading)
    val purchaseOptions = _purchaseOptions.asStateFlow()

    private val _shareState = MutableStateFlow<ShareState>(ShareState.NotPerformed)
    val shareState = _shareState.asStateFlow()

    val isPremium = _purchaseManager.isPremium


    val storages = _productRepo.getStoragesWithContainersShell().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyMap()
    )

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

            when (result) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, error = null) }
                is Result.Error -> _uiState.update { it.copy(isLoading = false, error = result.error.message) }
            }
        }
    }

    fun logOut() {
        _uiState.value = LoginUIState()
        viewModelScope.launch {
            _authRepo.logout()
        }
    }

    fun getPurchaseOptions() {
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
            _purchaseOptions.value = PurchaseOptionsState.Loading
            when (val result = _purchaseManager.purchase(subscription)) {
                is Result.Error -> _purchaseOptions.value = PurchaseOptionsState.Error(result.error.message)
                is Result.Success -> { /* NO-OP isPremium is auto collected */}
            }
        }
    }

    fun shareStorage(storage: Storage?, emails: Collection<String>) {
        if (storage == null) _shareState.value = ShareState.Error(Res.string.no_storage_chosen)
        else if (emails.isEmpty()) _shareState.value = ShareState.Error(Res.string.no_email_inputted)
        else {
            viewModelScope.launch {
                _shareState.value = ShareState.Loading
                //Do something here pls :)
            }
        }
    }

    fun resetShareStorageState() {
        _shareState.value = ShareState.NotPerformed
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

sealed interface ShareState {
    data object NotPerformed : ShareState
    data object Loading : ShareState
    data object Success : ShareState
    data class Error(val message: StringResource) : ShareState
}