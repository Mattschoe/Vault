package org.creategoodthings.vault.ui.pages.premium

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import org.creategoodthings.vault.ui.pages.PageShell
import org.jetbrains.compose.resources.stringResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.log_out

@Composable
fun RegisterPage(
    viewModel: LoginViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val purchaseOptions by viewModel.purchaseOptions.collectAsState()
    val shareState by viewModel.shareState.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val storages by viewModel.storages.collectAsState()
    val errorEmails by viewModel.errorEmails.collectAsState()
    var openPurchaseWindow by remember { mutableStateOf(false) }

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
        if (isPremium) {
            ShareStorageSection(
                shareState = shareState,
                errorEmails = errorEmails,
                storages = storages,
                onShare = viewModel::shareStorage,
                onResetShareState = viewModel::resetShareStorageState,
                padding = padding
            )
        } else if (!state.isSuccess) {
            LoginSection(
                uiState = state,
                onUsernameChange = viewModel::onUsernameChange,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onSubmit = { viewModel.submit(); openPurchaseWindow = true },
                onToggleMode = viewModel::toggleMode,
                padding = padding
            )
        } else {
            UnlockPremiumSection(
                purchaseOptions = purchaseOptions,
                isOpen = openPurchaseWindow,
                onOpen = { openPurchaseWindow = true },
                onDismiss = { openPurchaseWindow = false },
                onPurchase = viewModel::purchaseSubscription,
                onRetry = viewModel::getPurchaseOptions,
                padding = padding
            )
        }
    }
}
