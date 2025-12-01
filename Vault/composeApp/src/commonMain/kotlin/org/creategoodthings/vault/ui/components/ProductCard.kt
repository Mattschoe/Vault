package org.creategoodthings.vault.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.daysUntil
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.domain.calculateDaysRemaining
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.days
import vault.composeapp.generated.resources.expires_the
import vault.composeapp.generated.resources.months
import vault.composeapp.generated.resources.weeks

@Composable
fun ProductCard(product: Product, modifier: Modifier = Modifier) {
    //Formats the amount of days remaining into days, weeks and months
    val days = product.calculateDaysRemaining()
    val daysRemaining = when {
        days < 7 -> "$days ${stringResource(Res.string.days)}"
        days < 30 -> "${days/7} ${stringResource(Res.string.weeks)}"
        days < 365 -> "${days/30} ${stringResource(Res.string.months)}" //Every month is 30 now, but oh well
        else -> ">${days/365} ${stringResource(Res.string.months)}"
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
                    text = stringResource(Res.string.expires_the) + " ${product.bestBefore}",
                    color = Color.Gray
                )
            }
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
            ) {

                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    text = daysRemaining,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}