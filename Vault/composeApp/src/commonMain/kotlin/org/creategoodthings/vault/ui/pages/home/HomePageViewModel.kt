package org.creategoodthings.vault.ui.pages.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.domain.repositories.ProductRepository

class HomePageViewModel(private val _productRepo: ProductRepository): ViewModel() {


    val products =_productRepo.getProductsOrderedByBB().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun insertProduct(product: Product) {
        viewModelScope.launch {
            _productRepo.insertProduct(product)
        }
    }
}
