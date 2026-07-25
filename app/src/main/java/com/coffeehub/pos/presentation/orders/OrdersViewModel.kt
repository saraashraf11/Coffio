package com.coffeehub.pos.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeehub.pos.domain.model.Order
import com.coffeehub.pos.domain.model.OrderStatus
import com.coffeehub.pos.domain.repository.OrderRepository
import com.coffeehub.pos.domain.usecase.GetOrderHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val selectedStatus: OrderStatus? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getOrderHistoryUseCase: GetOrderHistoryUseCase,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getOrderHistoryUseCase.getAllOrders().collect { orders ->
                _uiState.update { state ->
                    val filtered = filterOrders(orders, state.selectedStatus, state.searchQuery)
                    state.copy(orders = filtered, isLoading = false)
                }
            }
        }
    }

    fun filterByStatus(status: OrderStatus?) {
        _uiState.update { it.copy(selectedStatus = status) }
        reloadOrders()
    }

    fun onSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        reloadOrders()
    }

    fun updateOrderStatus(orderId: Int, status: OrderStatus) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, status)
        }
    }

    private fun reloadOrders() {
        viewModelScope.launch {
            val status = _uiState.value.selectedStatus
            val flow = if (status != null)
                getOrderHistoryUseCase.getOrdersByStatus(status)
            else
                getOrderHistoryUseCase.getAllOrders()

            flow.collect { orders ->
                val filtered = filterOrders(orders, _uiState.value.selectedStatus, _uiState.value.searchQuery)
                _uiState.update { it.copy(orders = filtered) }
            }
        }
    }

    private fun filterOrders(orders: List<Order>, status: OrderStatus?, query: String): List<Order> {
        var filtered = if (status != null) orders.filter { it.status == status } else orders
        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.orderNumber.contains(query, ignoreCase = true) ||
                    it.customerName?.contains(query, ignoreCase = true) == true
            }
        }
        return filtered
    }
}
