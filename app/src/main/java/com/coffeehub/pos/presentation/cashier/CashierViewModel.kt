package com.coffeehub.pos.presentation.cashier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeehub.pos.domain.model.Category
import com.coffeehub.pos.domain.model.Product
import com.coffeehub.pos.domain.repository.CategoryRepository
import com.coffeehub.pos.domain.usecase.GetProductsByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CashierUiState(
    val categories: List<Category> = emptyList(),
    val products: List<Product> = emptyList(),
    val selectedCategoryId: Int? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class CashierViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase
) : ViewModel() {

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _uiState = MutableStateFlow(CashierUiState())
    val uiState: StateFlow<CashierUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getActiveCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
        loadProducts()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadProducts() {
        viewModelScope.launch {
            combine(_selectedCategoryId, _searchQuery) { cat, q -> Pair(cat, q) }
                .flatMapLatest { (cat, q) ->
                    if (q.isNotBlank()) getProductsByCategoryUseCase.searchProducts(q)
                    else getProductsByCategoryUseCase(cat)
                }
                .collect { products ->
                    _uiState.update { it.copy(products = products, isLoading = false) }
                }
        }
    }

    fun selectCategory(id: Int?) {
        _selectedCategoryId.value = id
        _uiState.update { it.copy(selectedCategoryId = id) }
    }

    fun onSearch(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }
}
