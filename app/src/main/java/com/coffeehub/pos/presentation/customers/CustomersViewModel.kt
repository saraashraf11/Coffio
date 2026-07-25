package com.coffeehub.pos.presentation.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeehub.pos.domain.model.Customer
import com.coffeehub.pos.domain.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomersUiState(
    val customers: List<Customer> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val editingCustomer: Customer? = null
)

@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomersUiState())
    val uiState: StateFlow<CustomersUiState> = _uiState.asStateFlow()
    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _searchQuery.debounce(300).flatMapLatest { query ->
                if (query.isBlank()) customerRepository.getAllCustomers()
                else customerRepository.searchCustomers(query)
            }.collect { customers ->
                _uiState.update { it.copy(customers = customers, isLoading = false) }
            }
        }
    }

    fun onSearchChange(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun showAddDialog() = _uiState.update { it.copy(showAddDialog = true, editingCustomer = null) }
    fun showEditDialog(customer: Customer) = _uiState.update { it.copy(showAddDialog = true, editingCustomer = customer) }
    fun hideDialog() = _uiState.update { it.copy(showAddDialog = false, editingCustomer = null) }

    fun saveCustomer(name: String, phone: String, email: String, notes: String) {
        viewModelScope.launch {
            val editing = _uiState.value.editingCustomer
            if (editing != null) {
                customerRepository.updateCustomer(editing.copy(name = name, phone = phone, email = email, notes = notes))
            } else {
                customerRepository.insertCustomer(Customer(name = name, phone = phone, email = email, notes = notes))
            }
            hideDialog()
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch { customerRepository.deleteCustomer(customer) }
    }
}
