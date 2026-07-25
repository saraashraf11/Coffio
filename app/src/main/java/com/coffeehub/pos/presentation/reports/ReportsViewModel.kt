package com.coffeehub.pos.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeehub.pos.domain.model.SalesReport
import com.coffeehub.pos.domain.usecase.GetSalesReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ReportPeriod { DAILY, WEEKLY, MONTHLY }

data class ReportsUiState(
    val selectedPeriod: ReportPeriod = ReportPeriod.DAILY,
    val report: SalesReport? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getSalesReportUseCase: GetSalesReportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init { loadReport(ReportPeriod.DAILY) }

    fun selectPeriod(period: ReportPeriod) {
        _uiState.update { it.copy(selectedPeriod = period, isLoading = true) }
        loadReport(period)
    }

    private fun loadReport(period: ReportPeriod) {
        viewModelScope.launch {
            val flow = when (period) {
                ReportPeriod.DAILY -> getSalesReportUseCase.getDailyReport()
                ReportPeriod.WEEKLY -> getSalesReportUseCase.getWeeklyReport()
                ReportPeriod.MONTHLY -> getSalesReportUseCase.getMonthlyReport()
            }
            flow.collect { report ->
                _uiState.update { it.copy(report = report, isLoading = false) }
            }
        }
    }
}
