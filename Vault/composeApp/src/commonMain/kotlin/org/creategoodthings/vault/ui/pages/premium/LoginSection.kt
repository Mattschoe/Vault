package org.creategoodthings.vault.ui.pages.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.creategoodthings.vault.ui.components.TextWithLink
import org.creategoodthings.vault.ui.pages.premium.LoginStateError.BLANK_PASSWORD
import org.creategoodthings.vault.ui.pages.premium.LoginStateError.BLANK_USERNAME
import org.creategoodthings.vault.ui.pages.premium.LoginStateError.INVALID_EMAIL
import org.creategoodthings.vault.ui.pages.premium.LoginStateError.NETWORK_ERROR
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.already_have_account
import vault.composeapp.generated.resources.create_account
import vault.composeapp.generated.resources.dont_have_account
import vault.composeapp.generated.resources.email
import vault.composeapp.generated.resources.hide_password
import vault.composeapp.generated.resources.invalid_email
import vault.composeapp.generated.resources.log_in
import vault.composeapp.generated.resources.network_error
import vault.composeapp.generated.resources.password
import vault.composeapp.generated.resources.password_required
import vault.composeapp.generated.resources.show_password
import vault.composeapp.generated.resources.sign_up
import vault.composeapp.generated.resources.username
import vault.composeapp.generated.resources.username_required
import vault.composeapp.generated.resources.visibility_off
import vault.composeapp.generated.resources.visibility_on
import vault.composeapp.generated.resources.welcome_back

@Composable
fun LoginSection(
    uiState: LoginUIState,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onToggleMode: () -> Unit,
    padding: PaddingValues
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(if (uiState.isRegisterMode) Res.string.create_account else Res.string.welcome_back),
            style = MaterialTheme.typography.headlineSmall
        )

        if (uiState.isRegisterMode) {
            OutlinedTextField(
                value = uiState.username,
                onValueChange = onUsernameChange,
                label = { Text(stringResource(Res.string.username)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.error == BLANK_USERNAME
            )
        }

        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(Res.string.email)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.error == INVALID_EMAIL
        )

        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(Res.string.password)) },
            singleLine = true,
            isError = uiState.error == BLANK_PASSWORD,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                val (image, description) =
                    if (passwordVisible) {
                        Pair(
                            vectorResource(Res.drawable.visibility_on),
                            stringResource(Res.string.hide_password)
                        )
                    } else {
                        Pair(
                            vectorResource(Res.drawable.visibility_off),
                            stringResource(Res.string.show_password)
                        )
                    }
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = image,
                        contentDescription = description
                    )
                }
            }
        )

        if (uiState.error != null) {
            Text(
                text = stringResource(when (uiState.error) {
                    BLANK_USERNAME -> Res.string.username_required
                    BLANK_PASSWORD -> Res.string.password_required
                    INVALID_EMAIL -> Res.string.invalid_email
                    NETWORK_ERROR -> Res.string.network_error
                }),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (uiState.isLoading) CircularProgressIndicator()
        else {
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(if (uiState.isRegisterMode) Res.string.sign_up else Res.string.log_in))
            }

            TextWithLink(
                prefixText = stringResource(if (uiState.isRegisterMode) Res.string.already_have_account else Res.string.dont_have_account),
                linkText = stringResource(if (uiState.isRegisterMode) Res.string.log_in else Res.string.sign_up),
                onSignUpClick = onToggleMode
            )
        }
    }
}
