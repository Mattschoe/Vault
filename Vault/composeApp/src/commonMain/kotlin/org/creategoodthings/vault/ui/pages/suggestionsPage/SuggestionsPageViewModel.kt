package org.creategoodthings.vault.ui.pages.suggestionsPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.domain.Storage
import org.creategoodthings.vault.domain.SuggestedProduct
import org.creategoodthings.vault.domain.repositories.PreferencesRepository
import org.creategoodthings.vault.domain.repositories.ProductRepository
import org.creategoodthings.vault.ui.RemindMeType
import org.creategoodthings.vault.ui.components.ProductDraft
import vault.composeapp.generated.resources.Oil
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.apples
import vault.composeapp.generated.resources.berries
import vault.composeapp.generated.resources.bread
import vault.composeapp.generated.resources.butter
import vault.composeapp.generated.resources.canned_tomato
import vault.composeapp.generated.resources.canned_tuna
import vault.composeapp.generated.resources.carrots
import vault.composeapp.generated.resources.chicken_breast
import vault.composeapp.generated.resources.coffee
import vault.composeapp.generated.resources.cold_cuts
import vault.composeapp.generated.resources.cream
import vault.composeapp.generated.resources.eggs
import vault.composeapp.generated.resources.fish
import vault.composeapp.generated.resources.flour
import vault.composeapp.generated.resources.fries
import vault.composeapp.generated.resources.frozen
import vault.composeapp.generated.resources.garlic
import vault.composeapp.generated.resources.juice
import vault.composeapp.generated.resources.lemons
import vault.composeapp.generated.resources.milk
import vault.composeapp.generated.resources.minced_beef
import vault.composeapp.generated.resources.oatmeal
import vault.composeapp.generated.resources.onion
import vault.composeapp.generated.resources.pantry
import vault.composeapp.generated.resources.pasta
import vault.composeapp.generated.resources.pizza
import vault.composeapp.generated.resources.potatoes
import vault.composeapp.generated.resources.refrigerated
import vault.composeapp.generated.resources.rice
import vault.composeapp.generated.resources.soup
import vault.composeapp.generated.resources.vegetables
import vault.composeapp.generated.resources.yoghurt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SuggestionsPageViewModel(
    private val _productRepo: ProductRepository,
    private val _prefRepo: PreferencesRepository
): ViewModel() {

    private val _today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val suggestions = listOf (
        SuggestionSection(
            titleID = Res.string.refrigerated,
            products = listOf(
                SuggestedProduct(Res.string.milk, _today.plus(7, DateTimeUnit.DAY), RemindMeType.DAYS, 1),
                SuggestedProduct(Res.string.butter, _today.plus(60, DateTimeUnit.DAY), RemindMeType.DAYS, 7),
                SuggestedProduct(Res.string.eggs, _today.plus(28, DateTimeUnit.DAY), RemindMeType.DAYS, 3),
                SuggestedProduct(Res.string.cream, _today.plus(8, DateTimeUnit.DAY), RemindMeType.DAYS, 2),
                SuggestedProduct(Res.string.yoghurt, _today.plus(20, DateTimeUnit.DAY), RemindMeType.DAYS, 3),
                SuggestedProduct(Res.string.cold_cuts, _today.plus(5, DateTimeUnit.DAY), RemindMeType.DAYS, 2),
                SuggestedProduct(Res.string.juice, _today.plus(21, DateTimeUnit.DAY), RemindMeType.DAYS, 3),
                SuggestedProduct(Res.string.carrots, _today.plus(14, DateTimeUnit.DAY), RemindMeType.DAYS, 3),
                SuggestedProduct(Res.string.lemons, _today.plus(21, DateTimeUnit.DAY), RemindMeType.DAYS, 3),
            )
        ),
        SuggestionSection(
            titleID = Res.string.pantry,
            products = listOf(
                SuggestedProduct(Res.string.pasta, _today.plus(730, DateTimeUnit.DAY), RemindMeType.DAYS, 30),
                SuggestedProduct(Res.string.rice, _today.plus(730, DateTimeUnit.DAY), RemindMeType.DAYS, 30),
                SuggestedProduct(Res.string.oatmeal, _today.plus(365, DateTimeUnit.DAY), RemindMeType.DAYS, 30),
                SuggestedProduct(Res.string.flour, _today.plus(365, DateTimeUnit.DAY), RemindMeType.DAYS, 30),
                SuggestedProduct(Res.string.canned_tomato, _today.plus(730, DateTimeUnit.DAY), RemindMeType.DAYS, 30),
                SuggestedProduct(Res.string.coffee, _today.plus(365, DateTimeUnit.DAY), RemindMeType.DAYS, 14),
                SuggestedProduct(Res.string.Oil, _today.plus(365, DateTimeUnit.DAY), RemindMeType.DAYS, 30),
                SuggestedProduct(Res.string.canned_tuna, _today.plus(1095, DateTimeUnit.DAY), RemindMeType.DAYS, 30),
                SuggestedProduct(Res.string.potatoes, _today.plus(14, DateTimeUnit.DAY), RemindMeType.DAYS, 3),
                SuggestedProduct(Res.string.onion, _today.plus(30, DateTimeUnit.DAY), RemindMeType.DAYS, 5),
                SuggestedProduct(Res.string.garlic, _today.plus(60, DateTimeUnit.DAY), RemindMeType.DAYS, 7),
                SuggestedProduct(Res.string.apples, _today.plus(7, DateTimeUnit.DAY), RemindMeType.DAYS, 2),


                )
        ),
        SuggestionSection(
            titleID = Res.string.frozen,
            products = listOf(
                SuggestedProduct(Res.string.pizza, _today.plus(180, DateTimeUnit.DAY), RemindMeType.DAYS, 7),
                SuggestedProduct(Res.string.fries, _today.plus(365, DateTimeUnit.DAY), RemindMeType.DAYS, 14),
                SuggestedProduct(Res.string.berries, _today.plus(365, DateTimeUnit.DAY), RemindMeType.DAYS, 14),
                SuggestedProduct(Res.string.fish, _today.plus(180, DateTimeUnit.DAY), RemindMeType.DAYS, 7),
                SuggestedProduct(Res.string.chicken_breast, _today.plus(270, DateTimeUnit.DAY), RemindMeType.DAYS, 14),
                SuggestedProduct(Res.string.minced_beef, _today.plus(120, DateTimeUnit.DAY), RemindMeType.DAYS, 7),
                SuggestedProduct(Res.string.butter, _today.plus(90, DateTimeUnit.DAY), RemindMeType.DAYS, 7),
                SuggestedProduct(Res.string.bread, _today.plus(90, DateTimeUnit.DAY), RemindMeType.DAYS, 3),
                SuggestedProduct(Res.string.vegetables, _today.plus(365, DateTimeUnit.DAY), RemindMeType.DAYS, 14),
                SuggestedProduct(Res.string.soup, _today.plus(90, DateTimeUnit.DAY), RemindMeType.DAYS, 7),
            )
        )
    )

    fun insertSuggestions(
        storage: Storage,
        drafts: List<ProductDraft>,
        onAdded: () -> Unit
    ) {
        viewModelScope.launch {
            _productRepo.insertStorage(storage)

            _prefRepo.setStandardStorageID(storage.ID)

            _productRepo.insertProducts(drafts.map { draft ->
                Product(
                    ID = draft.ID,
                    name = draft.name,
                    amount = draft.amount,
                    description = "",
                    storageID = storage.ID,
                    bestBefore = draft.bestBefore,
                    reminderDate = draft.reminderDate
                )
            })

            onAdded()
        }
    }
}