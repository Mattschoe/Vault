package org.creategoodthings.vault.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.creategoodthings.vault.domain.Container
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.domain.Storage
import org.creategoodthings.vault.ui.components.RemindMeType.DAYS
import org.creategoodthings.vault.ui.components.RemindMeType.MONTHS
import org.creategoodthings.vault.ui.components.RemindMeType.WEEKS
import org.creategoodthings.vault.ui.components.RemindMeType.YEARS
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.add_products
import vault.composeapp.generated.resources.allStringResources
import vault.composeapp.generated.resources.amount
import vault.composeapp.generated.resources.best_before
import vault.composeapp.generated.resources.best_before_error
import vault.composeapp.generated.resources.calendar_icon
import vault.composeapp.generated.resources.cancel
import vault.composeapp.generated.resources.days
import vault.composeapp.generated.resources.description
import vault.composeapp.generated.resources.dropdown_closed_icon
import vault.composeapp.generated.resources.dropdown_open_icon
import vault.composeapp.generated.resources.months
import vault.composeapp.generated.resources.ok
import vault.composeapp.generated.resources.product_name
import vault.composeapp.generated.resources.remind_me
import vault.composeapp.generated.resources.storage
import vault.composeapp.generated.resources.weeks
import vault.composeapp.generated.resources.years
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
@Composable
fun AddProductDialog(
    onClick: (Product) -> Unit,
    onDismiss: () -> Unit,
    storages: List<Storage>,
    containers: List<Container>
) {
    val spacingBetweenSections = 6.dp

    var showRemindMePicker by remember { mutableStateOf(false) }
    var remindMeType by remember { mutableStateOf(WEEKS) }
    var remindMeAmount by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val bestBefore = datePickerState.selectedDateMillis?.toLocalDate()

    var showStoragePicker by remember { mutableStateOf(false) }
    var storage: Storage? by remember { mutableStateOf(null) }
    var showContainerPicker by remember { mutableStateOf(false) }
    var container: Container? by remember { mutableStateOf(null) }
    var amount by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val remindMeDate: LocalDate? = remindMeAmount.toIntOrNull()?.let {
            bestBefore?.calculateReminder(it, remindMeType)
    }

    val isValid =
        amount.toIntOrNull().let { true } &&
        productName.trim().isNotEmpty() &&
        bestBefore?.isAfterToday() ?: false &&
        remindMeDate != null

    println(amount.toIntOrNull().let { true })
    println(productName.trim().isNotEmpty())
    println(bestBefore?.isAfterToday() ?: false)
    println(remindMeDate != null)

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
                //TITLE
                Text(
                    text = stringResource(Res.string.add_products),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                //region AMOUNT + NUMBER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    DialogOutlinedTextField(
                        value = amount,
                        onValueChange = {
                            if (it.toIntOrNull() != null) {
                                amount = it
                            }},
                        placeholder = stringResource(Res.string.amount),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                    )
                    Spacer(Modifier.width(spacingBetweenSections))
                    DialogOutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        placeholder = stringResource(Res.string.product_name),
                        modifier = Modifier
                            .weight(2f)
                    )
                }
                //endregion
                Spacer(Modifier.height(spacingBetweenSections))
                //DESCRIPTION
                DialogOutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = stringResource(Res.string.description),
                    singleLine = false,
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth(),
                )
                Spacer(Modifier.height(spacingBetweenSections))
                //region BEST BEFORE
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(Res.string.best_before),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier.width(190.dp)
                    ) {
                        DialogOutlinedTextField(
                            value = bestBefore?.toString() ?: "",
                            onValueChange = { /* READ ONLY */ },
                            readOnly = true,
                            placeholder = "",
                            trailingIcon = {
                                Icon(
                                    vectorResource(Res.drawable.calendar_icon),
                                    null
                                )
                            }
                        )

                        //onClick doesnt work for textfield, so this is fix
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showDatePicker = true }
                        )
                    }
                }
                //Error message
                if (bestBefore?.isAfterToday() == false) {
                    Text(
                        text = stringResource(Res.string.best_before_error),
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
                //endregion
                Spacer(Modifier.height(spacingBetweenSections))
                //region STORAGE
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(Res.string.storage),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier.width(190.dp)
                    ) {
                        DialogOutlinedTextField(
                            value = storage?.name ?: "",
                            onValueChange = { /* READ ONLY */ },
                            readOnly = true,
                            placeholder = "",
                            trailingIcon = {
                                Icon(vectorResource(
                                    if (showStoragePicker) Res.drawable.dropdown_open_icon
                                    else Res.drawable.dropdown_closed_icon
                                ),
                                    null
                                )
                            }
                        )

                        DropdownMenu(
                            expanded = showStoragePicker,
                            onDismissRequest = { showStoragePicker = false }
                        ) {
                            storages.forEach {
                                DropdownMenuItem(
                                    text = { Text(it.name) },
                                    onClick = {
                                        storage = it
                                        showStoragePicker = false
                                    }
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showStoragePicker = !showStoragePicker }
                        )
                    }
                }
                //endregion
                Spacer(Modifier.height(spacingBetweenSections))
                //region CONTAINER
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(Res.string.storage),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier.width(190.dp)
                    ) {
                        DialogOutlinedTextField(
                            value = container?.name ?: "",
                            onValueChange = { /* READ ONLY */ },
                            readOnly = true,
                            placeholder = "",
                            trailingIcon = {
                                Icon(vectorResource(
                                    if (showContainerPicker) Res.drawable.dropdown_open_icon
                                    else Res.drawable.dropdown_closed_icon
                                ),
                                    null
                                )
                            }
                        )

                        DropdownMenu(
                            expanded = showContainerPicker,
                            onDismissRequest = { showContainerPicker = false }
                        ) {
                            containers.forEach {
                                DropdownMenuItem(
                                    text = { Text(it.name) },
                                    onClick = {
                                        container = it
                                        showContainerPicker = false
                                    }
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showContainerPicker = !showContainerPicker }
                        )
                    }
                }
                //endregion
                Spacer(Modifier.height(spacingBetweenSections))
                //region REMINDER
                Text(
                    text = stringResource(Res.string.remind_me),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    DialogOutlinedTextField(
                        value = remindMeAmount,
                        onValueChange = {
                            if (it.toIntOrNull() != null) {
                                remindMeAmount = it
                            }},
                        placeholder = stringResource(Res.string.amount),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1.25f)
                    )
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        DialogOutlinedTextField(
                            value = remindMeType.ToString(),
                            onValueChange = { /* READ ONLY */ },
                            readOnly = true,
                            placeholder = "",
                            trailingIcon = {
                                Icon(vectorResource(
                                        if (showRemindMePicker) Res.drawable.dropdown_open_icon
                                        else Res.drawable.dropdown_closed_icon
                                    ),
                                    null
                                )
                            }
                        )

                        DropdownMenu(
                            expanded = showRemindMePicker,
                            onDismissRequest = { showRemindMePicker = false }
                        ) {
                            RemindMeType.entries.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.ToString()) },
                                    onClick = {
                                        remindMeType = type
                                        showRemindMePicker = false
                                    }
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showRemindMePicker = !showRemindMePicker }
                        )
                    }
                }
                //endregion
                Spacer(Modifier.height(spacingBetweenSections * 12)) //region OK + CANCEL
                //region OK + CANCEL
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
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
                        color = if (isValid) MaterialTheme.colorScheme.tertiary else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {

                            }
                    )
                }
                //endregion
            }
        }
    }

    //region DIALOGS
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(Res.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    //endregion
}

@Composable
fun DialogOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    trailingIcon: (@Composable () -> Unit)? = null,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(
            text = placeholder,
            fontStyle = FontStyle.Italic
        )},
        singleLine = singleLine,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier,
        minLines = minLines,
        trailingIcon = trailingIcon,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions
    )
}

@OptIn(ExperimentalTime::class)
fun Long.toLocalDate(): LocalDate {
    val instant = Instant.fromEpochMilliseconds(this)
    val timeZone = TimeZone.currentSystemDefault()
    return instant.toLocalDateTime(timeZone).date
}

@OptIn(ExperimentalTime::class)
fun LocalDate.isAfterToday(): Boolean {
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.todayIn(timeZone)
    return this > today
}

fun LocalDate.calculateReminder(amount: Int, type: RemindMeType): LocalDate {
    return when (type) {
        DAYS -> this.plus(amount, DateTimeUnit.DAY)
        WEEKS -> this.plus(amount * 7, DateTimeUnit.DAY)
        MONTHS -> this.plus(amount, DateTimeUnit.MONTH)
        YEARS -> this.plus(amount, DateTimeUnit.YEAR)
    }
}

enum class RemindMeType {
    DAYS,
    WEEKS,
    MONTHS,
    YEARS;
}

@Composable
fun RemindMeType.ToString(): String {
    return when (this) {
        DAYS -> stringResource(Res.string.days)
        WEEKS -> stringResource(Res.string.weeks)
        MONTHS -> stringResource(Res.string.months)
        YEARS -> stringResource(Res.string.years)
    }
}