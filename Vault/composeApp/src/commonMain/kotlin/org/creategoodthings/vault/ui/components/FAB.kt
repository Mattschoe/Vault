package org.creategoodthings.vault.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.add_icon
import vault.composeapp.generated.resources.add_product
import vault.composeapp.generated.resources.add_products
import vault.composeapp.generated.resources.check_icon

@Composable
fun AddProductFAB(modifier: Modifier = Modifier, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier
            .zIndex(1f)
    ) {
        Icon(
            vectorResource(Res.drawable.add_icon),
            contentDescription = stringResource(Res.string.add_product),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .size(36.dp)
        )
    }
}

@Composable
fun ConfirmFAB(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier
            .zIndex(1f)
    ) {
        Icon(
            vectorResource(Res.drawable.check_icon),
            contentDescription = stringResource(Res.string.add_products),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .size(36.dp)
        )
    }
}