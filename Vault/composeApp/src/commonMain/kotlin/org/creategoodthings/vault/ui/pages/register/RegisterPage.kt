package org.creategoodthings.vault.ui.pages.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.creategoodthings.vault.ui.pages.PageShell
import org.jetbrains.compose.resources.stringResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.already_have_account
import vault.composeapp.generated.resources.create_account
import vault.composeapp.generated.resources.dont_have_account
import vault.composeapp.generated.resources.email
import vault.composeapp.generated.resources.log_in
import vault.composeapp.generated.resources.password
import vault.composeapp.generated.resources.sign_up
import vault.composeapp.generated.resources.username
import vault.composeapp.generated.resources.welcome_back

@Composable
fun RegisterPage(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val state by viewModel.uiState.collectAsState()

    PageShell { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(if (state.isRegisterMode) Res.string.create_account else Res.string.welcome_back),
                style = MaterialTheme.typography.headlineSmall
            )

            if (state.isRegisterMode) {
                OutlinedTextField(
                    value = state.username,
                    onValueChange = viewModel::onUsernameChange,
                    label = { Text(stringResource(Res.string.username)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text(stringResource(Res.string.email)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text(stringResource(Res.string.password)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (state.isLoading) CircularProgressIndicator()
            else {
                Button(
                    onClick = viewModel::submit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(if (state.isRegisterMode) Res.string.sign_up else Res.string.log_in))
                }

                TextButton(
                    onClick = viewModel::toggleMode
                ) {
                    Text(stringResource(if (state.isRegisterMode) Res.string.already_have_account else Res.string.dont_have_account))
                }
            }
        }
    }
}