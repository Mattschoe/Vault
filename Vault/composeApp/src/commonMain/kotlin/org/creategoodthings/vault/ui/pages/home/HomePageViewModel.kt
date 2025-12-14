package org.creategoodthings.vault.ui.pages.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.creategoodthings.vault.domain.Container
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
    private val _products = _productRepo.getProductsOrderedByBB()
    private val _storages = _productRepo.getStoragesWithContainersShell()

    private val _selectedStorageID = _preferencesRepo.standardStorageID
    private val _selectedStorage = _selectedStorageID.flatMapLatest { ID ->
        if (ID == null) {
            flowOf(StorageUIState.NoneSelected)
        } else {
            _productRepo.getStorageWithProducts(ID).map {
                if (it != null) StorageUIState.Success(it)
                else StorageUIState.NoneSelected
            }
        }
    }

    val uiState = combine(
        _products,
        _storages,
        _selectedStorage
    ) { products, storages, selectedStorage ->
        DataUIState.Ready(
            products = products,
            storages = storages,
            selectedStorage = selectedStorage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DataUIState.Loading
    )


    fun addProduct(product: Product) {
        viewModelScope.launch {
            _productRepo.insertProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            _productRepo.deleteProduct(product)
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

    fun addContainer(container: Container) {
        viewModelScope.launch {
            _productRepo.insertContainer(container)
        }
    }
}

sealed interface DataUIState {
    data object Loading : DataUIState
    data class Ready(
        val products: List<Product>,
        val storages: Map<Storage, List<Container>>,
        val selectedStorage: StorageUIState
    ) : DataUIState
}

sealed interface StorageUIState {
    data object Loading : StorageUIState
    data object NoneSelected : StorageUIState
    data class Success(val data: StorageWithProducts) : StorageUIState
}


