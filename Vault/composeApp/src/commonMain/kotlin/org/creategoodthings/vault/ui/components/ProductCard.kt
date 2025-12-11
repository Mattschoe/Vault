package org.creategoodthings.vault.ui.components

import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month.*
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.domain.calculateDaysRemaining
import org.creategoodthings.vault.ui.theme.MustardContainer
import org.creategoodthings.vault.ui.theme.OnMustardContainer
import org.jetbrains.compose.resources.stringResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.days
import vault.composeapp.generated.resources.expired
import vault.composeapp.generated.resources.expires
import vault.composeapp.generated.resources.month_apr
import vault.composeapp.generated.resources.month_aug
import vault.composeapp.generated.resources.month_dec
import vault.composeapp.generated.resources.month_feb
import vault.composeapp.generated.resources.month_jan
import vault.composeapp.generated.resources.month_jul
import vault.composeapp.generated.resources.month_jun
import vault.composeapp.generated.resources.month_mar
import vault.composeapp.generated.resources.month_may
import vault.composeapp.generated.resources.month_nov
import vault.composeapp.generated.resources.month_oct
import vault.composeapp.generated.resources.month_sep
import vault.composeapp.generated.resources.months
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
    val bbDate = product.bestBefore.toDisplayString()

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

/**
 * The state of the product, transports info like daysRemaining, textColor and backgroundColor
 */
data class ProductStateInfo(
    val daysRemaining: String,
    val textColor: Color,
    val containerColor: Color
)

@Composable
fun LocalDate.toDisplayString(): String {
    val monthResource = when(this.month) {
        JANUARY -> Res.string.month_jan
        FEBRUARY -> Res.string.month_feb
        MARCH -> Res.string.month_mar
        APRIL -> Res.string.month_apr
        MAY -> Res.string.month_may
        JUNE -> Res.string.month_jun
        JULY -> Res.string.month_jul
        AUGUST -> Res.string.month_aug
        SEPTEMBER -> Res.string.month_sep
        OCTOBER -> Res.string.month_oct
        NOVEMBER -> Res.string.month_nov
        DECEMBER -> Res.string.month_dec
    }
    val monthName = stringResource(monthResource)
    return "$day. $monthName ${this.year}"
}