package org.creategoodthings.vault.ui.pages.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.creategoodthings.vault.domain.Container
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.domain.Storage
import org.creategoodthings.vault.domain.StorageID
import org.creategoodthings.vault.domain.repositories.ContainerWithProducts
import org.creategoodthings.vault.domain.repositories.PreferencesRepository
import org.creategoodthings.vault.domain.repositories.ProductRepository
import org.creategoodthings.vault.domain.services.NotificationScheduler
import org.creategoodthings.vault.domain.services.PermissionController
import org.creategoodthings.vault.domain.services.PurchaseManager
import org.creategoodthings.vault.domain.services.SyncManager
import org.creategoodthings.vault.ui.pages.home.StorageUIState
import org.creategoodthings.vault.ui.pages.storage.SortOption.*

@OptIn(ExperimentalCoroutinesApi::class)
class StoragePageViewModel(
    private val _storageID: String,
    private val _productRepo: ProductRepository,
    private val _prefRepo: PreferencesRepository,
    private val _notificationScheduler: NotificationScheduler,
    private val _permissionController: PermissionController,
    private val _syncManager: SyncManager,
    private val _purchaseManager: PurchaseManager,
): ViewModel() {
    val isPremium = _purchaseManager.isPremium

    val storages = _productRepo.getStoragesWithContainersShell().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyMap()
    )

    private val _selectedStorageID = _prefRepo.standardStorageID
    val selectedStorage = _selectedStorageID.flatMapLatest { ID ->
        if (ID == null) {
            flowOf(StorageUIState.NoneSelected)
        } else {
            _productRepo.getStorageWithProducts(ID).map {
                if (it != null) StorageUIState.Success(it)
                else StorageUIState.NoneSelected
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StorageUIState.Loading
    )

    private val _sortOption = _prefRepo.sortOption.map { option ->
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
                _prefRepo.containerSortOrder.flatMapLatest { sortOrder ->
                    combine(
                        _productRepo.getStorageContainersWithProducts(_storageID, sortOrder),
                        _productRepo.getStorageProductsWithoutContainer(_storageID, sortOrder)
                    ) { groups, unOrganizedProducts ->
                        ProductListData.Grouped(groups, unOrganizedProducts)
                    }
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
            _prefRepo.setSortOption(newOption)
        }
    }

    fun updateStorageName(newName: String) {
        viewModelScope.launch {
            _productRepo.updateStorage(Storage(ID = StorageID(_storageID), name = newName))
            if (isPremium.value == true) _syncManager.startSync()
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            _productRepo.insertProduct(product)
            if (isPremium.value == true) _syncManager.startSync()
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            _productRepo.deleteProduct(product)
            if (isPremium.value == true) _syncManager.startSync()
        }
    }

    fun addStorage(storage: Storage, changeToStore: Boolean = false) {
        viewModelScope.launch {
            _productRepo.insertStorage(storage)
            if (changeToStore) _prefRepo.setStandardStorageID(storage.ID)
            if (isPremium.value == true) _syncManager.startSync()
        }
    }

    fun addContainer(container: Container) {
        viewModelScope.launch {
            _productRepo.insertContainer(container)
            if (isPremium.value == true) _syncManager.startSync()
        }
    }

    /**
     * Changes the container which a product belongs to.
     * @param newContainer if passed null, will remove the container from the product (effectively moving it into "unorganized")
     */
    fun changeProductContainer(product: Product, newContainer: Container?) {
        viewModelScope.launch {
            _productRepo.updateProduct(product.copy(containerID = newContainer?.ID))
            if (isPremium.value == true) _syncManager.startSync()
        }
    }
}

sealed interface ProductListData {
    data class Grouped(
        val groups: List<ContainerWithProducts>,
        val unorganizedProducts: List<Product>
    ) : ProductListData
    data class Flat(val products: List<Product>) : ProductListData
}

enum class SortOption {
    ALPHABET,
    CONTAINER,
    BEST_BEFORE
}
