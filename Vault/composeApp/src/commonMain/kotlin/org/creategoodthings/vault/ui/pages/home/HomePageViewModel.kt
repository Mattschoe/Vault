package org.creategoodthings.vault.ui.pages.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.domain.Storage
import org.creategoodthings.vault.domain.repositories.PreferencesRepository
import org.creategoodthings.vault.domain.repositories.ProductRepository
import org.creategoodthings.vault.domain.repositories.StorageWithProducts

@OptIn(ExperimentalCoroutinesApi::class)
class HomePageViewModel(
    private val _productRepo: ProductRepository,
    private val _preferencesRepo: PreferencesRepository
): ViewModel() {
    val products = _productRepo.getProductsOrderedByBB().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
    val storages = _productRepo.getStoragesWithContainersShell().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyMap()
    )

    private val _selectedStorageID = _preferencesRepo.standardStorageID
    val selectedStorage: StateFlow<StorageUIState> = _selectedStorageID.flatMapLatest { ID ->
        if (ID == null) {
            flowOf(StorageUIState.NoneSelected)
        } else {
            _productRepo.getStorageWithProducts(ID).map {
                StorageUIState.Success(it)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StorageUIState.Loading
    )


    fun insertProduct(product: Product) {
        viewModelScope.launch {
            _productRepo.insertProduct(product)
        }
    }

    fun changeStorage(newStorage: Storage) {
        viewModelScope.launch {
            _preferencesRepo.setStandardStorageID(newStorage.ID)
        }
    }

    fun addStorage(storage: Storage, changeToStore: Boolean = false) {
        viewModelScope.launch {
            _productRepo.insertStorage(storage)
            if (changeToStore) _preferencesRepo.setStandardStorageID(storage.ID)
        }
    }
}

sealed interface StorageUIState {
    data object Loading : StorageUIState
    data object NoneSelected : StorageUIState
    data class Success(val data: StorageWithProducts) : StorageUIState
}


