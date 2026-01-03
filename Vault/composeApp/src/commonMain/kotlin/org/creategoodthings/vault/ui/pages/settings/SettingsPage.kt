package org.creategoodthings.vault.ui.pages.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlinx.datetime.LocalTime
import org.creategoodthings.vault.domain.repositories.ContainerSortOrder
import org.creategoodthings.vault.domain.repositories.ContainerSortOrder.ALPHABETICALLY
import org.creategoodthings.vault.domain.repositories.ContainerSortOrder.BEST_BEFORE
import org.creategoodthings.vault.ui.pages.PageShell
import org.creategoodthings.vault.ui.toDisplayString
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.account_header
import vault.composeapp.generated.resources.alphabetically
import vault.composeapp.generated.resources.am_pm_enabled
import vault.composeapp.generated.resources.best_before
import vault.composeapp.generated.resources.cancel
import vault.composeapp.generated.resources.dropdown_closed_icon
import vault.composeapp.generated.resources.dropdown_open_icon
import vault.composeapp.generated.resources.ok
import vault.composeapp.generated.resources.reminder_header
import vault.composeapp.generated.resources.reminder_time_body
import vault.composeapp.generated.resources.settings
import vault.composeapp.generated.resources.sort_order_in_container
import vault.composeapp.generated.resources.storage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    val reminderTime by viewModel.reminderTime.collectAsState()
    val amPm by viewModel.amPm.collectAsState()
    var showTimePickerDialog by remember { mutableStateOf(false) }

    val containerSortOrder by viewModel.containerSortOrder.collectAsState()
    val choices = remember { ContainerSortOrder.entries.map {
        DropdownOption(
            value = it,
            displayText = it.getStringResource()
        )
    }}

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
            //region STORAGE
            item { SectionDivider(stringResource(Res.string.storage))}
            //Storage order in container
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.sort_order_in_container),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(16.dp))
                    SettingsDropDown(
                        choices = choices,
                        onSelect = { viewModel.changeContainerSortOrder(it) },
                        selectedValue = DropdownOption(containerSortOrder, containerSortOrder.getStringResource())
                    )
                }
            }

            //endregion

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

fun ContainerSortOrder.getStringResource(): StringResource {
   return when(this) {
       ALPHABETICALLY -> Res.string.alphabetically
       BEST_BEFORE -> Res.string.best_before
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SettingsDropDown(
    modifier: Modifier = Modifier,
    choices: List<DropdownOption<T>>,
    selectedValue: DropdownOption<T>,
    onSelect: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.padding(start = 4.dp)
    ) {
        Surface(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .wrapContentWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.Transparent,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(selectedValue.displayText),
                    style = MaterialTheme.typography.bodyLarge
                )
                Icon(
                    imageVector = vectorResource(if (expanded) Res.drawable.dropdown_open_icon else Res.drawable.dropdown_closed_icon),
                    contentDescription = null
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            choices.forEach { choice ->
                DropdownMenuItem(
                    text = { Text(stringResource(choice.displayText)) },
                    onClick = {
                        onSelect(choice.value)
                        expanded = false
                    }
                )
            }
        }
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

data class DropdownOption<T>(
    val value: T,
    val displayText: StringResource
)

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