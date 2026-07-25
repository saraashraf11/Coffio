package com.coffeehub.pos.presentation.tables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeehub.pos.domain.model.CoffeeTable
import com.coffeehub.pos.domain.model.TableStatus
import com.coffeehub.pos.domain.repository.TableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TablesUiState(
    val tables: List<CoffeeTable> = emptyList(),
    val selectedTableId: Int? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class TablesViewModel @Inject constructor(
    private val tableRepository: TableRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TablesUiState())
    val uiState: StateFlow<TablesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            tableRepository.getAllTables().collect { tables ->
                _uiState.update { it.copy(tables = tables, isLoading = false) }
            }
        }
    }

    fun selectTable(tableId: Int) {
        _uiState.update { it.copy(selectedTableId = tableId) }
    }

    fun updateTableStatus(tableId: Int, status: TableStatus) {
        viewModelScope.launch {
            tableRepository.updateTableStatus(tableId, status, null)
        }
    }
}
