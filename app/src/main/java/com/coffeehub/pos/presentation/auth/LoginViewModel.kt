package com.coffeehub.pos.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeehub.pos.data.preferences.UserPreferencesManager
import com.coffeehub.pos.domain.model.User
import com.coffeehub.pos.domain.repository.UserRepository
import com.coffeehub.pos.utils.PasswordUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedInUser: User? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) =
        _uiState.update { it.copy(username = value, error = null) }

    fun onPasswordChange(value: String) =
        _uiState.update { it.copy(password = value, error = null) }

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val username = _uiState.value.username.trim()
            val password = _uiState.value.password

            if (username.isBlank() || password.isBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Please enter username and password"
                    )
                }
                return@launch
            }

            val passwordHash = PasswordUtils.hash(password)
            val user = userRepository.authenticate(username, passwordHash)

            if (user != null) {
                preferencesManager.saveUserSession(user.id, user.name, user.role.name)
                userRepository.updateLastLogin(user.id)
                _uiState.update { it.copy(isLoading = false, loggedInUser = user) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Invalid username or password"
                    )
                }
            }
        }
    }
}
