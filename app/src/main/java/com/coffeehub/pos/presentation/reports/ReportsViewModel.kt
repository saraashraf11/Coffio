package com.coffeehub.pos.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeehub.pos.domain.model.SalesReport
import com.coffeehub.pos.domain.usecase.GetSalesReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ReportPeriod { DAILY, WEEKLY, MONTHLY }

data class ReportsUiState(
    val selectedPeriod: ReportPeriod = ReportPeriod.DAILY,
    val report: SalesReport? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getSalesReportUseCase: GetSalesReportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    // Keep a reference so we can cancel the previous job when period changes
    private var reportJob: Job? = null

    init {
        loadReport(ReportPeriod.DAILY)
    }

    fun selectPeriod(period: ReportPeriod) {
        if (_uiState.value.selectedPeriod == period) return
        _uiState.update { it.copy(selectedPeriod = period, isLoading = true, error = null) }
        loadReport(period)
    }

    private fun loadReport(period: ReportPeriod) {
        // Cancel any running collection before starting a new one
        reportJob?.cancel()
        reportJob = viewModelScope.launch {
            val flow = when (period) {
                ReportPeriod.DAILY   -> getSalesReportUseCase.getDailyReport()
                ReportPeriod.WEEKLY  -> getSalesReportUseCase.getWeeklyReport()
                ReportPeriod.MONTHLY -> getSalesReportUseCase.getMonthlyReport()
            }
            flow
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { report ->
                    _uiState.update { it.copy(report = report, isLoading = false, error = null) }
                }
        }
    }
}
