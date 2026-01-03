package org.creategoodthings.vault.ui.pages.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlinx.datetime.LocalTime
import org.creategoodthings.vault.domain.repositories.PreferencesRepository
import org.creategoodthings.vault.ui.pages.PageShell
import org.creategoodthings.vault.ui.toDisplayString
import org.jetbrains.compose.resources.stringResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.account_header
import vault.composeapp.generated.resources.am_pm_enabled
import vault.composeapp.generated.resources.cancel
import vault.composeapp.generated.resources.delete_me
import vault.composeapp.generated.resources.ok
import vault.composeapp.generated.resources.reminder_header
import vault.composeapp.generated.resources.reminder_time_body
import vault.composeapp.generated.resources.settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    val reminderTime by viewModel.reminderTime.collectAsState()
    val amPm by viewModel.amPm.collectAsState()
    var showTimePickerDialog by remember { mutableStateOf(false) }

    PageShell { padding ->
        LazyColumn(
            contentPadding = padding
        ) {
            //TITLE
            item {
                Text(
                    text = stringResource(Res.string.settings),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            //region REMINDER
            item { SectionDivider(stringResource(Res.string.reminder_header)) }
            //Reminder time
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.reminder_time_body)
                    )

                    SettingsButton(
                        text = reminderTime.toDisplayString(amPm),
                        onClick = { showTimePickerDialog = true },
                    )
                }
            }

            //AM/PM
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(Res.string.am_pm_enabled))
                    Switch(
                        checked = amPm,
                        onCheckedChange = { viewModel.setAmPm(it) }
                    )
                }
            }
            //endregion

            //region ACCOUNT
            item { SectionDivider(stringResource(Res.string.account_header)) }
            item {
                //SettingsButton(text = stringResource(Res.string.delete_me))
            }
            //endregion
        }
    }

    //region DIALOGS
    if (showTimePickerDialog) {
        TimePickerDialog(
            initialTime = reminderTime,
            amPm = !amPm, //Appearntly "false" = show AM/PM, which is fucking dumb
            onConfirm = { newTime ->
                viewModel.setReminderTime(newTime)
                showTimePickerDialog = false
            },
            onDismiss = { showTimePickerDialog = false }
        )
    }
    //endregion
}

@Composable
fun SettingsButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Text(text = text)
    }
}

@Composable
fun SectionDivider(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            color = Color.Gray.copy(alpha = 0.3f),
            thickness = 1.dp
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )

        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            color = Color.Gray.copy(alpha = 0.3f),
            thickness = 1.dp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: LocalTime,
    amPm: Boolean = false,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = amPm
    )
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 6.dp,
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                TimePicker(timePickerState)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(Res.string.cancel),
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onDismiss() }
                    )

                    Text(
                        text = stringResource(Res.string.ok),
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onConfirm(LocalTime(timePickerState.hour, timePickerState.minute)) }
                    )
                }
            }
        }
    }
}