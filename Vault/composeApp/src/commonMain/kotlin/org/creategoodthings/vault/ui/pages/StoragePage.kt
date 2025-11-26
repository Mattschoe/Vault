package org.creategoodthings.vault.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.edit
import vault.composeapp.generated.resources.edit_icon

@Composable
fun StoragePage(
    storageID: String,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    //val storage = viewModel.storage(storageID)
    //val groupOrder by viewModel.groupOrder.collectAsState()

    PageShell(
        modifier = modifier,
    ) { padding ->
        LazyColumn(
            modifier = modifier
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Text(
                            text = "Lagertitel",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Icon(
                            imageVector = vectorResource(Res.drawable.edit_icon),
                            contentDescription = stringResource(Res.string.edit),
                            modifier = Modifier
                                .clickable { }
                        )
                    }
                }
            }
        }
    }
}