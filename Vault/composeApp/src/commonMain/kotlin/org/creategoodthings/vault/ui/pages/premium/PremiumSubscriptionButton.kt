package org.creategoodthings.vault.ui.pages.premium

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.creategoodthings.vault.domain.services.SubscriptionOption
import org.creategoodthings.vault.ui.theme.OnMustardContainer
import org.jetbrains.compose.resources.stringResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.month

@Composable
fun PremiumSubscriptionButton(
    option: SubscriptionOption,
    displayPricePerMonth: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val pricePerMonth =
        if (displayPricePerMonth) "/ ${option.pricePerMonth} ${stringResource(Res.string.month)}"
        else "/ ${stringResource(Res.string.month)}"

    Card(
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(2.dp, OnMustardContainer),
        modifier = Modifier
            .clickable { onSelect() }
    ) {
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
