package org.creategoodthings.vault.ui.pages.storage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.creategoodthings.vault.ui.components.ProductCard
import org.creategoodthings.vault.ui.pages.PageShell
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.edit
import vault.composeapp.generated.resources.edit_icon
import org.creategoodthings.vault.ui.pages.storage.ProductListData.*
import org.creategoodthings.vault.ui.pages.storage.SortOption.*
import vault.composeapp.generated.resources.alphabet_icon
import vault.composeapp.generated.resources.calendar_icon
import vault.composeapp.generated.resources.category_icon
import vault.composeapp.generated.resources.sorted_alphabetically
import vault.composeapp.generated.resources.sorted_bb
import vault.composeapp.generated.resources.sorted_containers
import vault.composeapp.generated.resources.unorganized

@Composable
fun StoragePage(
    navController: NavController,
    viewModel:  StoragePageViewModel,
    modifier: Modifier = Modifier,
) {
    val sortOption by viewModel.sortOption.collectAsState()
    val state by viewModel.products.collectAsState()

    PageShell(
        modifier = modifier,
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = modifier
        ) {
            //region TITEL + SORT ORDER
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
                    Icon(
                        imageVector = vectorResource(
                            when (sortOption) {
                                ALPHABET -> Res.drawable.alphabet_icon
                                BEST_BEFORE -> Res.drawable.calendar_icon
                                CONTAINER -> Res.drawable.category_icon
                            }
                        ),
                        contentDescription = stringResource(
                            when (sortOption) {
                                ALPHABET -> Res.string.sorted_alphabetically
                                BEST_BEFORE -> Res.string.sorted_bb
                                CONTAINER -> Res.string.sorted_containers
                            }
                        ),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { viewModel.toggleBetweenSortOption() }
                    )
                }
            }
            //endregion

            item {
                Spacer(Modifier.height(48.dp))
            }

            when (val state = state) {
                is Flat -> {
                    items(
                        items = state.products,
                        key = { it.ID }
                    ) { product ->
                        ProductCard(
                            product = product,
                            modifier = Modifier
                                .animateItem()
                        )
                    }
                }

                is Grouped -> {
                    state.groups.forEach { (container, products) ->
                        item(key = container.ID) {
                            ContainerText(container.name)
                            Spacer(Modifier.height(4.dp))
                        }
                        items(
                            items = products,
                            key = { it.ID }
                        ) { product ->
                            ProductCard(
                                product = product,
                                modifier = Modifier
                                    .animateItem()
                            )
                        }
                        item {
                            Spacer(Modifier.height(24.dp))
                        }
                    }

                    item {
                        ContainerText(stringResource(Res.string.unorganized))
                    }
                    items(
                        items = state.unOrganizedProducts,
                        key = { it.ID }
                    ) { product ->
                        ProductCard(
                            product = product,
                            modifier = Modifier
                                .animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContainerText(text: String) {
    Text(
        text = text,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodyLarge
    )
}