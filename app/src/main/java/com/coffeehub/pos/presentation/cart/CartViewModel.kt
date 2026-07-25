package com.coffeehub.pos.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeehub.pos.domain.usecase.PlaceOrderUseCase
import com.coffeehub.pos.domain.model.CartItem
import com.coffeehub.pos.domain.model.MilkType
import com.coffeehub.pos.domain.model.OrderType
import com.coffeehub.pos.domain.model.PaymentMethod
import com.coffeehub.pos.domain.model.Product
import com.coffeehub.pos.domain.model.ProductSize
import com.coffeehub.pos.domain.model.Temperature
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val orderType: OrderType = OrderType.DINE_IN,
    val selectedTableId: Int? = null,
    val selectedTableNumber: Int? = null,
    val selectedCustomerId: Int? = null,
    val discount: Double = 0.0,
    val taxRate: Double = 0.14,
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val isPlacingOrder: Boolean = false,
    val lastOrderId: Long? = null,
    val error: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val placeOrderUseCase: PlaceOrderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun addProduct(product: Product) {
        val existing = _uiState.value.cartItems.find {
            it.product.id == product.id &&
                it.selectedSize == ProductSize.MEDIUM &&
                it.selectedTemperature == Temperature.HOT &&
                it.selectedMilkType == MilkType.WHOLE
        }
        if (existing != null) {
            increaseQuantity(existing.id)
        } else {
            val defaultSize = product.availableSizes.firstOrNull() ?: ProductSize.MEDIUM
            val defaultTemp = product.availableTemperatures.firstOrNull() ?: Temperature.HOT
            val defaultMilk = product.availableMilkTypes.firstOrNull() ?: MilkType.WHOLE
            val newItem = CartItem(
                product = product,
                selectedSize = defaultSize,
                selectedTemperature = defaultTemp,
                selectedMilkType = defaultMilk
            )
            _uiState.update { state ->
                val newCart = state.cartItems + newItem
                state.copy(cartItems = newCart).recalculate()
            }
        }
    }

    fun removeItem(cartItemId: String) {
        _uiState.update { state ->
            state.copy(cartItems = state.cartItems.filter { it.id != cartItemId }).recalculate()
        }
    }

    fun increaseQuantity(cartItemId: String) {
        _uiState.update { state ->
            val newCart = state.cartItems.map {
                if (it.id == cartItemId) it.copy(quantity = it.quantity + 1) else it
            }
            state.copy(cartItems = newCart).recalculate()
        }
    }

    fun decreaseQuantity(cartItemId: String) {
        val item = _uiState.value.cartItems.find { it.id == cartItemId }
        if (item != null && item.quantity == 1) {
            removeItem(cartItemId)
        } else {
            _uiState.update { state ->
                val newCart = state.cartItems.map {
                    if (it.id == cartItemId) it.copy(quantity = it.quantity - 1) else it
                }
                state.copy(cartItems = newCart).recalculate()
            }
        }
    }

    fun clearCart() {
        _uiState.update { CartUiState() }
    }

    fun setOrderType(orderType: OrderType) {
        _uiState.update { it.copy(orderType = orderType) }
    }

    fun setTable(tableId: Int, tableNumber: Int) {
        _uiState.update { it.copy(selectedTableId = tableId, selectedTableNumber = tableNumber) }
    }

    fun setCustomer(customerId: Int) {
        _uiState.update { it.copy(selectedCustomerId = customerId) }
    }

    fun setDiscount(discount: Double) {
        _uiState.update { it.copy(discount = discount).recalculate() }
    }

    fun placeOrder(paymentMethod: PaymentMethod, cashierName: String, cashReceived: Double = 0.0) {
        viewModelScope.launch {
            _uiState.update { it.copy(isPlacingOrder = true, error = null) }
            val state = _uiState.value
            val result = placeOrderUseCase(
                cartItems = state.cartItems,
                orderType = state.orderType,
                tableId = state.selectedTableId,
                customerId = state.selectedCustomerId,
                paymentMethod = paymentMethod,
                cashierName = cashierName,
                taxRate = state.taxRate,
                discount = state.discount,
                cashReceived = cashReceived
            )
            result.fold(
                onSuccess = { orderId ->
                    _uiState.update { it.copy(isPlacingOrder = false, lastOrderId = orderId) }
                    clearCart()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isPlacingOrder = false, error = error.message) }
                }
            )
        }
    }

    private fun CartUiState.recalculate(): CartUiState {
        val subtotal = cartItems.sumOf { it.subtotal }
        val tax = subtotal * taxRate
        val total = subtotal + tax - discount
        return copy(subtotal = subtotal, tax = tax, total = total)
    }
}
