package org.creategoodthings.vault.ui.pages.premium

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.creategoodthings.vault.domain.Storage
import org.creategoodthings.vault.ui.isValidEmail
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.add_email
import vault.composeapp.generated.resources.add_members
import vault.composeapp.generated.resources.add_person_icon
import vault.composeapp.generated.resources.check_circle_icon
import vault.composeapp.generated.resources.choose_storage
import vault.composeapp.generated.resources.close_icon
import vault.composeapp.generated.resources.dropdown_closed_icon
import vault.composeapp.generated.resources.dropdown_open_icon
import vault.composeapp.generated.resources.invalid_email
import vault.composeapp.generated.resources.remove_email
import vault.composeapp.generated.resources.select_storage
import vault.composeapp.generated.resources.share
import vault.composeapp.generated.resources.share_storage
import vault.composeapp.generated.resources.successful_share
import vault.composeapp.generated.resources.welcome_premium_title

@Composable
fun ShareStorageSection(
    shareState: ShareState,
    errorEmails: List<String>,
    storages: Map<Storage, *>,
    onShare: (Storage?, Collection<String>) -> Unit,
    onResetShareState: () -> Unit,
    padding: PaddingValues
) {
    var emailInput by remember { mutableStateOf("") }
    val emails = remember { mutableStateSetOf<String>() }
    var hasEmailError by remember { mutableStateOf(false) }
    var showChooseStorage by remember { mutableStateOf(false) }
    var selectedStorage by remember { mutableStateOf<Storage?>(null) }

    fun addEmail() {
        val trimmed = emailInput.trim()
        if (trimmed.isValidEmail()) {
            if (trimmed !in emails) {
                emails.add(trimmed)
                emailInput = ""
                hasEmailError = false
            }
        } else {
            hasEmailError = true
        }
    }

    LazyColumn(
        contentPadding = padding,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = stringResource(Res.string.welcome_premium_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item { Spacer(Modifier.height(24.dp)) }
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth()
                    .fillMaxHeight(),
                elevation = CardDefaults.cardElevation(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight(0.3f)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.add_person_icon),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(Res.string.share_storage),
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    //region 1. Select storage
                    Text(
                        text = "1. ${stringResource(Res.string.select_storage)}",
                        fontWeight = FontWeight.SemiBold
                    )
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { showChooseStorage = !showChooseStorage }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = selectedStorage?.name ?: stringResource(Res.string.choose_storage)
                            )
                            Icon(
                                imageVector = vectorResource(
                                    if (showChooseStorage) Res.drawable.dropdown_open_icon
                                    else Res.drawable.dropdown_closed_icon
                                ),
                                contentDescription = stringResource(Res.string.choose_storage),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        DropdownMenu(
                            expanded = showChooseStorage,
                            onDismissRequest = { showChooseStorage = false },
                        ) {
                            storages.forEach {
                                val storage = it.key
                                DropdownMenuItem(
                                    text = { Text(storage.name) },
                                    onClick = {
                                        onResetShareState()
                                        selectedStorage = storage
                                        showChooseStorage = false
                                    }
                                )
                            }
                        }
                    }
                    //endregion
                    Spacer(Modifier.height(36.dp))
                    //region 2. Add members
                    Text(
                        text = "2. ${stringResource(Res.string.add_members)}",
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = {
                                emailInput = it
                                hasEmailError = false
                                onResetShareState()
                            },
                            label = { Text(stringResource(Res.string.add_email)) },
                            supportingText = { if (hasEmailError) stringResource(Res.string.invalid_email) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { addEmail() }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            modifier = Modifier
                                .size(48.dp)
                                .aspectRatio(1f)
                                .clickable { addEmail() },
                            imageVector = vectorResource(Res.drawable.check_circle_icon),
                            tint = MaterialTheme.colorScheme.tertiary,
                            contentDescription = stringResource(Res.string.add_email),
                        )
                    }
                    if (hasEmailError) {
                        Text(
                            text = stringResource(Res.string.invalid_email),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        emails.forEach { email ->
                            AnimatedVisibility(
                                visible = true,
                                enter = scaleIn(),
                                exit = scaleOut()
                            ) {
                                InputChip(
                                    selected = true,
                                    onClick = { emails.remove(email) },
                                    label = { Text(email) },
                                    trailingIcon = {
                                        Icon(
                                            vectorResource(Res.drawable.close_icon),
                                            contentDescription = stringResource(Res.string.remove_email),
                                            modifier = Modifier.size(InputChipDefaults.IconSize)
                                        )
                                    },
                                    border = InputChipDefaults.inputChipBorder(
                                        enabled = true,
                                        selected = true,
                                        borderColor = MaterialTheme.colorScheme.primary
                                    ),
                                )
                            }
                        }
                    }
                    //endregion
                    Spacer(Modifier.height(24.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onShare(selectedStorage, emails) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            when (shareState) {
                                is ShareState.NotPerformed -> {
                                    Text(
                                        text = stringResource(Res.string.share),
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                is ShareState.Error -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = stringResource(shareState.message),
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        errorEmails.forEach { email ->
                                            Text(
                                                text = email,
                                                color = MaterialTheme.colorScheme.error,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                                ShareState.Loading -> {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onTertiary
                                    )
                                }
                                ShareState.Success -> {
                                    Text(
                                        text = stringResource(Res.string.successful_share),
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
