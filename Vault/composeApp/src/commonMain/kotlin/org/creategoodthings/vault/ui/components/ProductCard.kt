package org.creategoodthings.vault.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.domain.calculateDaysRemaining
import org.creategoodthings.vault.ui.theme.MustardContainer
import org.creategoodthings.vault.ui.theme.MustardWarning
import org.jetbrains.compose.resources.stringResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.days
import vault.composeapp.generated.resources.expired
import vault.composeapp.generated.resources.expired_the
import vault.composeapp.generated.resources.expires_the
import vault.composeapp.generated.resources.months
import vault.composeapp.generated.resources.weeks
import vault.composeapp.generated.resources.year

@Composable
fun ProductCard(product: Product, modifier: Modifier = Modifier) {
    //Formats the amount of days remaining into days, weeks and months
    val days = product.calculateDaysRemaining()
    val stateInfo = when {
        days < 0 -> ProductStateInfo("$days ${stringResource(Res.string.expired).uppercase()}", Color.Black, MaterialTheme.colorScheme.error)
        days < 7 -> ProductStateInfo("$days ${stringResource(Res.string.days)}", MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondaryContainer)
        days < 30 -> ProductStateInfo("${days/7} ${stringResource(Res.string.weeks)}", MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondaryContainer)
        days < 365 -> ProductStateInfo("${days/30} ${stringResource(Res.string.months)}", MustardWarning, MustardContainer) //Every month is 30 now, but oh well
        else -> ProductStateInfo(">${days/365} ${stringResource(Res.string.year)}", MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.tertiaryContainer)
    }

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
            Column(
            ) {
                Text(
                    text = product.name,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text =
                        if (days < 0) stringResource(Res.string.expired_the) + " ${product.bestBefore}"
                        else stringResource(Res.string.expires_the) + " ${product.bestBefore}",
                    color = Color.Gray
                )
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

/**
 * The state of the product, transports info like daysRemaining, textColor and backgroundColor
 */
data class ProductStateInfo(
    val daysRemaining: String,
    val textColor: Color,
    val containerColor: Color
)