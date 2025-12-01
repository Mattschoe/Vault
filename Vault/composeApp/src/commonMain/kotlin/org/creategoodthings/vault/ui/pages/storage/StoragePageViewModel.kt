package org.creategoodthings.vault.ui.pages.storage




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


enum class SortOption {
    ALPHABET,
    STORAGE,
    BEST_BEFORE
}