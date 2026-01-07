package org.creategoodthings.vault.ui.pages.premium

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.creategoodthings.vault.domain.services.SubscriptionOption
import org.creategoodthings.vault.ui.pages.PageShell
import org.creategoodthings.vault.ui.theme.MustardContainer
import org.creategoodthings.vault.ui.theme.OnMustardContainer
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.already_have_account
import vault.composeapp.generated.resources.and_get
import vault.composeapp.generated.resources.cloud_storage
import vault.composeapp.generated.resources.create_account
import vault.composeapp.generated.resources.dont_have_account
import vault.composeapp.generated.resources.email
import vault.composeapp.generated.resources.lets_go
import vault.composeapp.generated.resources.log_in
import vault.composeapp.generated.resources.log_out
import vault.composeapp.generated.resources.month
import vault.composeapp.generated.resources.password
import vault.composeapp.generated.resources.premium_icon
import vault.composeapp.generated.resources.sharing_storage_with_family
import vault.composeapp.generated.resources.sign_up
import vault.composeapp.generated.resources.unlock_premium
import vault.composeapp.generated.resources.username
import vault.composeapp.generated.resources.welcome_back
import org.creategoodthings.vault.ui.pages.premium.PurchaseOptionsState.*
import vault.composeapp.generated.resources.check_icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val purchaseOptions by viewModel.purchaseOptions.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openPurchaseWindow by remember { mutableStateOf(false) }

    PageShell { padding ->
        if (!state.isSuccess) {
            //region LOG IN/REGISTER
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
                        onClick = {
                            viewModel.submit()
                            openPurchaseWindow = true
                        },
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
            //endregion
        } else if (state.isSuccess) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                TextButton(
                    onClick = { openPurchaseWindow = true }
                ) {
                    Text("Unlock premium")
                }

                TextButton(
                    onClick = viewModel::logOut
                ) {
                    Text(stringResource(Res.string.log_out))
                }
            }

            if (openPurchaseWindow) {
                ModalBottomSheet(
                    onDismissRequest = { openPurchaseWindow = false },
                    sheetState = sheetState,
                    containerColor = MustardContainer,
                    contentColor = OnMustardContainer,
                    modifier = Modifier.padding(top = 144.dp)
                ) {
                    //region UNLOCK PREMIUM
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.premium_icon),
                                modifier = Modifier.size(144.dp),
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(Res.string.unlock_premium),
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(36.dp))
                            when(val state = purchaseOptions) {
                                Loading -> CircularProgressIndicator()
                                is Success -> {
                                    val monthSub = state.options[0]
                                    val yearSub = state.options[1]
                                    var selectedSubscription by remember { mutableStateOf(yearSub) }

                                    //This is very dumb, but i cant figure out a smart way other than hardcode this
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier
                                    ) {
                                        PremiumSubscriptionButton(
                                            option = monthSub,
                                            displayPricePerMonth = false,
                                            isSelected = selectedSubscription == monthSub,
                                            onSelect = { selectedSubscription = monthSub }
                                        )
                                        PremiumSubscriptionButton(
                                            option = yearSub,
                                            displayPricePerMonth = true,
                                            isSelected = selectedSubscription == yearSub,
                                            onSelect = { selectedSubscription = yearSub }
                                        )
                                    }
                                    Spacer(Modifier.height(36.dp))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row {
                                            Icon(
                                                imageVector = vectorResource(Res.drawable.check_icon),
                                                tint = MaterialTheme.colorScheme.tertiary,
                                                contentDescription = null
                                            )
                                            Text(
                                                text = stringResource(Res.string.cloud_storage),
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                        Row {
                                            Icon(
                                                imageVector = vectorResource(Res.drawable.check_icon),
                                                tint = MaterialTheme.colorScheme.tertiary,
                                                contentDescription = null
                                            )
                                            Text(
                                                text = stringResource(Res.string.sharing_storage_with_family),
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                    Spacer(Modifier.weight(1f))
                                    TextButton(
                                        onClick = { viewModel.purchaseSubscription(selectedSubscription) },
                                        colors = ButtonDefaults.textButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        modifier = Modifier
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = stringResource(Res.string.lets_go),
                                            fontSize = 24.sp,
                                            fontStyle = FontStyle.Italic,
                                            modifier = Modifier.padding(horizontal = 24.dp)
                                        )
                                    }
                                }
                                is Error -> {
                                    //TODO Better error handling
                                    Text(
                                        text = state.message,
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    //endregion
                }
            }

        }
    }
}

@Composable
fun PremiumSubscriptionButton(
    option: SubscriptionOption,
    displayPricePerMonth: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val pricePerMonth = if (displayPricePerMonth) {
        "/ ${option.pricePerMonth} ${stringResource(Res.string.month)}"
    } else {
        "/ ${stringResource(Res.string.month)}"
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(2.dp, OnMustardContainer),
        modifier = Modifier
            .clickable { onSelect() }
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, start = 6.dp, end = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )
            Text(
                text = option.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = option.fullPrice,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = pricePerMonth,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
            }
        }
    }
}