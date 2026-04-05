package org.creategoodthings.vault.ui.pages.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.creategoodthings.vault.domain.services.SubscriptionOption
import org.creategoodthings.vault.ui.pages.premium.PurchaseOptionsState.Error
import org.creategoodthings.vault.ui.pages.premium.PurchaseOptionsState.Loading
import org.creategoodthings.vault.ui.pages.premium.PurchaseOptionsState.Success
import org.creategoodthings.vault.ui.theme.MustardContainer
import org.creategoodthings.vault.ui.theme.OnMustardContainer
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.check_icon
import vault.composeapp.generated.resources.cloud_storage
import vault.composeapp.generated.resources.lets_go
import vault.composeapp.generated.resources.premium_icon
import vault.composeapp.generated.resources.sharing_storage_with_family
import vault.composeapp.generated.resources.try_again
import vault.composeapp.generated.resources.unlock_premium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnlockPremiumSection(
    purchaseOptions: PurchaseOptionsState,
    isOpen: Boolean,
    onOpen: () -> Unit,
    onDismiss: () -> Unit,
    onPurchase: (SubscriptionOption) -> Unit,
    onRetry: () -> Unit,
    padding: PaddingValues
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        TextButton(onClick = onOpen) { Text("Unlock premium") }
    }

    if (isOpen) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MustardContainer,
            contentColor = OnMustardContainer,
            modifier = Modifier.padding(top = 144.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
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
                    when (purchaseOptions) {
                        Loading -> CircularProgressIndicator()
                        is Success -> {
                            val monthSub = purchaseOptions.options[0]
                            val yearSub = purchaseOptions.options[1]
                            var selectedSubscription by remember { mutableStateOf(yearSub) }

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
                                modifier = Modifier.fillMaxWidth(),
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
                                onClick = { onPurchase(selectedSubscription) },
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.padding(12.dp)
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
                            Text(
                                text = purchaseOptions.message,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.weight(1f))
                            TextButton(
                                onClick = onRetry,
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = stringResource(Res.string.try_again),
                                    fontSize = 24.sp,
                                    fontStyle = FontStyle.Italic,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
