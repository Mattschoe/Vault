package org.creategoodthings.vault.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.creategoodthings.vault.domain.Container
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.domain.SuggestedProduct
import org.creategoodthings.vault.domain.calculateDaysRemaining
import org.creategoodthings.vault.ui.theme.MustardContainer
import org.creategoodthings.vault.ui.theme.OnMustardContainer
import org.creategoodthings.vault.ui.toDisplayText
import org.creategoodthings.vault.ui.toLocaleDisplayDate
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.add_icon
import vault.composeapp.generated.resources.add_product
import vault.composeapp.generated.resources.best_before
import vault.composeapp.generated.resources.calendar_icon
import vault.composeapp.generated.resources.cancel
import vault.composeapp.generated.resources.check_icon
import vault.composeapp.generated.resources.days
import vault.composeapp.generated.resources.dropdown_closed_icon
import vault.composeapp.generated.resources.dropdown_open_icon
import vault.composeapp.generated.resources.expired
import vault.composeapp.generated.resources.expires
import vault.composeapp.generated.resources.items
import vault.composeapp.generated.resources.minus_icon
import vault.composeapp.generated.resources.months
import vault.composeapp.generated.resources.ok
import vault.composeapp.generated.resources.remind_me
import vault.composeapp.generated.resources.weeks
import vault.composeapp.generated.resources.year

@Composable
fun ProductCard(product: Product, modifier: Modifier = Modifier) {
    //Formats the amount of days remaining into days, weeks and months
    val days = product.calculateDaysRemaining()
    val stateInfo = when {
        days < 0 -> ProductStateInfo(stringResource(Res.string.expired).uppercase(), MaterialTheme.colorScheme.onError, MaterialTheme.colorScheme.error)
        days < 7 -> ProductStateInfo("$days ${stringResource(Res.string.days)}", MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.colorScheme.secondaryContainer)
        days < 30 -> ProductStateInfo("${days/7} ${stringResource(Res.string.weeks)}", MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.colorScheme.secondaryContainer)
        days < 365 -> ProductStateInfo("${days/30} ${stringResource(Res.string.months)}", OnMustardContainer, MustardContainer) //Every month is 30 now, but oh well
        else -> ProductStateInfo(">${days/365} ${stringResource(Res.string.year)}", MaterialTheme.colorScheme.onTertiaryContainer, MaterialTheme.colorScheme.tertiaryContainer)
    }
    val bbDate = product.bestBefore.toDisplayText()

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier
                        .size(30.dp)
                ) {
                    Text(
                        text = product.amount.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column(
                ) {
                    Text(
                        text = product.name,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text =
                            if (days < 0) stringResource(Res.string.expired) + " " + bbDate
                            else stringResource(Res.string.expires) + " " + bbDate,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = stateInfo.containerColor
                ),

            ) {

                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    text = stateInfo.daysRemaining,
                    color = stateInfo.textColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DraggableProductCard(
    product: Product,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    ProductCard(
        product = product,
        modifier = modifier
            .pointerInput(product.ID) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDragStart(offset)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() }
                )
            }
    )
}

    @Composable
    fun SuggestedProductCard(
        product: SuggestedProduct,
        onAdd: (Product) -> Unit,
        modifier: Modifier = Modifier
    ) {
        var isExpanded by remember { mutableStateOf(false) }
        var showDatePicker by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()
        var amount by remember { mutableStateOf(1) }
        var bestBefore by remember { mutableStateOf(datePickerState.selectedDateMillis?.toLocalDate() ?: product.suggestedBestBefore) }
        var showRemindMePicker by remember { mutableStateOf(false) }
        var remindMeType by remember { mutableStateOf(product.suggestedReminderType) }
        var remindMeAmount by remember { mutableStateOf(product.suggestedReminderAmount.toString()) }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                .clickable { isExpanded = !isExpanded }
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                //region TITLE + ICON
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = product.name,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    AnimatedContent(
                        targetState = isExpanded,
                        transitionSpec = { (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut()) },
                        label = "IconAnimation"
                    ) { expanded ->
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            val resource = if (expanded) vectorResource(Res.drawable.check_icon)
                            else vectorResource(Res.drawable.add_icon)
                            Icon(
                                imageVector = resource,
                                contentDescription = stringResource(Res.string.add_product),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                    }
                }
                //endregion

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 16.dp)
                    ) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        Spacer(Modifier.height(12.dp))

                        //region BEST BEFORE + AMOUNT
                        Column(
                            modifier = modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(Res.string.best_before),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    DialogOutlinedTextField(
                                        value = bestBefore.toLocaleDisplayDate(Locale.current),
                                        onValueChange = { /* READ ONLY */ },
                                        readOnly = true,
                                        placeholder = "",
                                        trailingIcon = {
                                            Icon(
                                                vectorResource(Res.drawable.calendar_icon),
                                                null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    //onClick doesnt work for textfield, so this is fix
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clickable { showDatePicker = true }
                                    )
                                }
                                Spacer(Modifier.width(4.dp))
                                //AMOUNT
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                        ),
                                        modifier = Modifier
                                            .clickable {
                                                if (amount > 1) amount--
                                                else isExpanded = false
                                            }
                                    ) {
                                        Icon(
                                            imageVector = vectorResource(Res.drawable.minus_icon),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                    Text(
                                        text = amount.toString() + " " + stringResource(Res.string.items),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                    Card(
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                        ),
                                        modifier = Modifier
                                            .clickable { amount++ }
                                    ) {
                                        Icon(
                                            imageVector = vectorResource(Res.drawable.add_icon),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                }
                            }
                        }
                        //endregion
                        Spacer(Modifier.height(24.dp))
                        //region REMINDER
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(Res.string.remind_me),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            DialogOutlinedTextField(
                                value = remindMeAmount,
                                onValueChange = { remindMeAmount = it },
                                placeholder = "",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(0.4f)
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
                    }
                }
            }
        }
        //region DIALOGS
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = { showDatePicker = false }
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

/// Drag state holder
data class DragState(
    val draggedProduct: Product? = null,
    val dragOffset: Offset = Offset.Zero,
    val itemSize: IntSize = IntSize.Zero,
    val isDragging: Boolean = false
)

/// Container drop zone data
data class DropZone(
    val zoneID: String,
    val container: Container? = null,
    val bounds: Rect,
    val center: Offset = Offset.Zero
)

/**
 * The state of the product, transports info like daysRemaining, textColor and backgroundColor
 */
data class ProductStateInfo(
    val daysRemaining: String,
    val textColor: Color,
    val containerColor: Color
)