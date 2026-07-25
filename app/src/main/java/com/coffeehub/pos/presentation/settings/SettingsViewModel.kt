package com.coffeehub.pos.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeehub.pos.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val shopName: String = "BrewPoint POS",
    val taxRate: Double = 0.14,
    val isDarkTheme: Boolean = true,
    val currency: String = "$",
    val userName: String = "",
    val userRole: String = "",
    val language: String = "en"  // "en" or "ar"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                preferencesManager.shopName,
                preferencesManager.taxRate,
                preferencesManager.isDarkTheme,
                preferencesManager.userName,
                preferencesManager.userRole
            ) { shopName, taxRate, isDark, name, role ->
                SettingsUiState(shopName = shopName, taxRate = taxRate, isDarkTheme = isDark, userName = name, userRole = role)
            }.collect { state -> _uiState.value = state }
        }
        viewModelScope.launch {
            preferencesManager.language.collect { lang ->
                _uiState.update { it.copy(language = lang) }
            }
        }
    }

    fun setShopName(name: String) {
        viewModelScope.launch { preferencesManager.setShopName(name) }
    }

    fun setTaxRate(rate: Double) {
        viewModelScope.launch { preferencesManager.setTaxRate(rate) }
    }

    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch { preferencesManager.setDarkTheme(isDark) }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch { preferencesManager.setLanguage(lang) }
    }
}

