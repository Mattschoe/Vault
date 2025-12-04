package org.creategoodthings.vault.ui.pages.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.domain.Storage
import org.creategoodthings.vault.domain.repositories.ContainerWithProducts
import org.creategoodthings.vault.domain.repositories.PreferencesRepository
import org.creategoodthings.vault.domain.repositories.ProductRepository
import org.creategoodthings.vault.ui.pages.storage.SortOption.*

class StoragePageViewModel(
    private val _storageID: String,
    private val _productRepo: ProductRepository,
    private val _preferenceRepo: PreferencesRepository
): ViewModel() {

    private val _sortOption = _preferenceRepo.sortOption.map { option ->
        when(option) {
            ALPHABET -> ALPHABET
            CONTAINER -> CONTAINER
            BEST_BEFORE -> BEST_BEFORE
            null -> BEST_BEFORE
        }
    }
    val sortOption = _sortOption.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BEST_BEFORE
    )

    val storageName = _productRepo.getStorageName(_storageID).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ""
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val products = _sortOption.flatMapLatest { option ->
        when (option) {
            CONTAINER -> {
                combine(
                    _productRepo.getStorageContainersWithProductsOrderedByBB(_storageID),
                    _productRepo.getStorageProductsWithoutContainerOrderedByBB(_storageID)
                ) { groups, unOrganizedProducts ->
                    ProductListData.Grouped(groups, unOrganizedProducts)
                }
            }

            ALPHABET -> {
                _productRepo.getAllProductsOrderedByAlphabet().map {
                    ProductListData.Flat(it)
                }
            }

            BEST_BEFORE -> {
                _productRepo.getProductsOrderedByBB().map {
                    ProductListData.Flat(it)
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProductListData.Flat(emptyList())
    )

    /**
     * Toggles between the different sort options
     */
    fun toggleBetweenSortOption() {
        val newOption = when(sortOption.value) {
            ALPHABET -> CONTAINER
            CONTAINER -> BEST_BEFORE
            BEST_BEFORE -> ALPHABET
        }
        viewModelScope.launch {
            _preferenceRepo.setSortOption(newOption)
        }
    }

    fun updateStorageName(newName: String) {
        viewModelScope.launch {
            _productRepo.updateStorage(Storage(ID = _storageID, name = newName))
        }
    }
}

sealed interface ProductListData {
    data class Grouped(
        val groups: List<ContainerWithProducts>,
        val unOrganizedProducts: List<Product>
    ) : ProductListData
    data class Flat(val products: List<Product>) : ProductListData
}

enum class SortOption {
    ALPHABET,
    CONTAINER,
    BEST_BEFORE
}

/*
USE THIS IN STORAGE VIEWMODEL
private val _sortOption = MutableStateFlow(SortOption.BEST_BEFORE)
@OptIn(ExperimentalCoroutinesApi::class)
val products = _sortOption.flatMapLatest { sortOrder ->
    when (sortOrder) {
        ALPHABET -> _productRepo.getAllProductsOrderedByAlphabet()
        STORAGE -> _productRepo.getStorageWithProductsOrderedByBB()
        BEST_BEFORE -> _productRepo.getProductsOrderedByBB()
    }
}.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = emptyList()
)
*/