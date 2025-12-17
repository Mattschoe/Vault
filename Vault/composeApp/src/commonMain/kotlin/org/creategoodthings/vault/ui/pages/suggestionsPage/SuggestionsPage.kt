package org.creategoodthings.vault.ui.pages.suggestionsPage

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.domain.Storage
import org.creategoodthings.vault.domain.SuggestedProduct
import org.creategoodthings.vault.ui.components.AddProductFAB
import org.creategoodthings.vault.ui.components.ConfirmFAB
import org.creategoodthings.vault.ui.components.ProductDraft
import org.creategoodthings.vault.ui.components.SuggestedProductCard
import org.creategoodthings.vault.ui.components.WelcomeDialog
import org.creategoodthings.vault.ui.navigation.PageNavigation
import org.creategoodthings.vault.ui.pages.PageShell
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.suggestions_page_title

@Composable
fun SuggestionsPage(
    navController: NavController,
    viewModel: SuggestionsPageViewModel
) {
    val suggestions = viewModel.suggestions
    var storage by remember { mutableStateOf<Storage?>(null) }
    val drafts = remember { mutableMapOf<String, ProductDraft>() }

    PageShell(
        floatingActionButton = {
            ConfirmFAB(
                onClick = {
                    storage?.let {
                        viewModel.insertSuggestions(
                            storage =storage!!,
                            drafts = drafts.values.toList(),
                            onAdded = { navController.navigate(PageNavigation.Home) }
                        )
                    }
                }
            )
        }
    ){ padding ->
        LazyColumn(
            contentPadding = padding
        ) {
            item {
                Text(
                    text = stringResource(Res.string.suggestions_page_title),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(Modifier.height(16.dp))
            }

            items(suggestions) { section ->
                Text(
                    text = stringResource(section.titleID),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )

                section.products.forEach { product ->
                    SuggestedProductCard(
                        product = product,
                        onAddOrUpdate = { drafts[it.ID] = it },
                        onRemove = { drafts.remove(it) }
                    )
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }

    if (storage == null) {
        WelcomeDialog(
            onConfirm = { storage = it }
        )
    }
}

data class SuggestionSection(
    val titleID: StringResource,
    val products: List<SuggestedProduct>
)