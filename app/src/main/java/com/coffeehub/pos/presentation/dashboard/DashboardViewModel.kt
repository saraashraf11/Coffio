package com.coffeehub.pos.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeehub.pos.data.preferences.UserPreferencesManager
import com.coffeehub.pos.domain.model.Order
import com.coffeehub.pos.domain.model.Product
import com.coffeehub.pos.domain.repository.OrderRepository
import com.coffeehub.pos.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val userName: String = "",
    val userRole: String = "",
    val todayRevenue: Double = 0.0,
    val todayOrders: Int = 0,
    val activeOrders: Int = 0,
    val recentOrders: List<Order> = emptyList(),
    val popularProducts: List<Product> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            // Load user preferences
            launch {
                preferencesManager.userName.collect { name ->
                    _uiState.update { it.copy(userName = name) }
                }
            }
            launch {
                preferencesManager.userRole.collect { role ->
                    _uiState.update { it.copy(userRole = role) }
                }
            }
            // Load orders
            launch {
                orderRepository.getAllOrders().collect { orders ->
                    val activeCount = orders.count { it.status.name in listOf("PENDING", "IN_PROGRESS", "READY") }
                    val todayRevenue = orders.filter { it.status.name == "COMPLETED" }.sumOf { it.total }
                    _uiState.update {
                        it.copy(
                            recentOrders = orders.take(5),
                            activeOrders = activeCount,
                            todayRevenue = todayRevenue,
                            todayOrders = orders.size,
                            isLoading = false
                        )
                    }
                }
            }
            // Load popular products
            launch {
                productRepository.getPopularProducts().collect { products ->
                    _uiState.update { it.copy(popularProducts = products.take(6)) }
                }
            }
        }
    }
}
