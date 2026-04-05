package org.creategoodthings.vault.ui.pages.premium

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.creategoodthings.vault.ui.pages.PageShell
import org.jetbrains.compose.resources.stringResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.log_out

@Composable
fun RegisterPage(
    viewModel: LoginViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val purchaseOptions by viewModel.purchaseOptions.collectAsStateWithLifecycle()
    val shareState by viewModel.shareState.collectAsStateWithLifecycle()
    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
    val storages by viewModel.storages.collectAsStateWithLifecycle()
    val errorEmails by viewModel.errorEmails.collectAsStateWithLifecycle()
    var openPurchaseWindow by remember { mutableStateOf(state.isSuccess && isPremium == false) }

    PageShell(
        bottomBar = {
            if (state.isSuccess) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = viewModel::logOut) {
                        Text(stringResource(Res.string.log_out))
                    }
                }
            }
        }
    ) { padding ->
        if (!state.isSuccess) {
            LoginSection(
                uiState = state,
                onUsernameChange = viewModel::onUsernameChange,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onSubmit = { viewModel.submit(); openPurchaseWindow = true },
                onToggleMode = viewModel::toggleMode,
                padding = padding
            )
        } else if (isPremium == true) {
            ShareStorageSection(
                shareState = shareState,
                errorEmails = errorEmails,
                storages = storages,
                onShare = viewModel::shareStorage,
                onResetShareState = viewModel::resetShareStorageState,
                padding = padding
            )
        } else if (isPremium == false) {
            UnlockPremiumSection(
                purchaseOptions = purchaseOptions,
                isOpen = openPurchaseWindow,
                onOpen = { openPurchaseWindow = true },
                onDismiss = { openPurchaseWindow = false },
                onPurchase = viewModel::purchaseSubscription,
                onRetry = viewModel::getPurchaseOptions,
                onLogout = viewModel::logOut,
                padding = padding
            )
        }
        // isPremium == null: premium state still loading, show nothing
    }
}
