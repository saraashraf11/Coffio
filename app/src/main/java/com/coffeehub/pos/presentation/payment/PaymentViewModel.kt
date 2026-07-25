package com.coffeehub.pos.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeehub.pos.data.preferences.UserPreferencesManager
import com.coffeehub.pos.domain.model.PaymentMethod
import com.coffeehub.pos.presentation.cart.CartViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentUiState(
    val selectedMethod: PaymentMethod = PaymentMethod.CASH,
    val cashReceived: String = "",
    val change: Double = 0.0,
    val cashierName: String = "",
    val isProcessing: Boolean = false,
    val isSuccess: Boolean = false
)

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.userName.collect { name ->
                _uiState.update { it.copy(cashierName = name) }
            }
        }
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        _uiState.update { it.copy(selectedMethod = method) }
    }

    fun onCashReceivedChange(value: String, orderTotal: Double) {
        val received = value.toDoubleOrNull() ?: 0.0
        val change = maxOf(0.0, received - orderTotal)
        _uiState.update { it.copy(cashReceived = value, change = change) }
    }

    fun processPayment(cartViewModel: CartViewModel) {
        val state = _uiState.value
        _uiState.update { it.copy(isProcessing = true) }
        cartViewModel.placeOrder(
            paymentMethod = state.selectedMethod,
            cashierName = state.cashierName,
            cashReceived = state.cashReceived.toDoubleOrNull() ?: 0.0
        )
        _uiState.update { it.copy(isProcessing = false, isSuccess = true) }
    }
}
