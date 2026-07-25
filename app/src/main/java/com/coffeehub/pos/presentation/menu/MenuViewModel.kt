package com.coffeehub.pos.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeehub.pos.domain.model.Category
import com.coffeehub.pos.domain.model.Product
import com.coffeehub.pos.domain.repository.CategoryRepository
import com.coffeehub.pos.domain.repository.ProductRepository
import com.coffeehub.pos.domain.usecase.GetProductsByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MenuUiState(
    val categories: List<Category> = emptyList(),
    val products: List<Product> = emptyList(),
    val selectedCategoryId: Int? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        loadProducts()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getActiveCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadProducts() {
        viewModelScope.launch {
            combine(_selectedCategoryId, _searchQuery) { categoryId, query ->
                Pair(categoryId, query)
            }.flatMapLatest { (categoryId, query) ->
                if (query.isNotBlank()) {
                    getProductsByCategoryUseCase.searchProducts(query)
                } else {
                    getProductsByCategoryUseCase(categoryId)
                }
            }.collect { products ->
                _uiState.update { it.copy(products = products, isLoading = false) }
            }
        }
    }

    fun selectCategory(categoryId: Int?) {
        _selectedCategoryId.value = categoryId
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepository.deleteProduct(product)
        }
    }

    fun toggleAvailability(product: Product) {
        viewModelScope.launch {
            productRepository.toggleProductAvailability(product.id, !product.isAvailable)
        }
    }
}
