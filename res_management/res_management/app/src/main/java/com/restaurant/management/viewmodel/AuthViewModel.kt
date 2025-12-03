package com.restaurant.management.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.restaurant.management.data.model.User
import com.restaurant.management.data.model.UserRole
import com.restaurant.management.data.repository.UserRepository
import com.restaurant.management.util.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            preferencesManager.isLoggedIn.collect { isLoggedIn ->
                if (isLoggedIn) {
                    preferencesManager.userId.collect { userId ->
                        userId?.let {
                            val user = userRepository.getUserById(it)
                            _currentUser.value = user
                            _authState.value = AuthState.Authenticated(user!!)
                        }
                    }
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val result = userRepository.login(email, password)
            
            result.onSuccess { user ->
                _currentUser.value = user
                preferencesManager.saveUserSession(
                    userId = user.id,
                    email = user.email,
                    name = user.name,
                    role = user.role.name
                )
                _authState.value = AuthState.Authenticated(user)
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Login failed")
            }
        }
    }

    fun register(name: String, email: String, password: String, role: UserRole) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val user = User(
                name = name,
                email = email,
                password = password,
                role = role
            )
            
            val result = userRepository.register(user)
            
            result.onSuccess { registeredUser ->
                _currentUser.value = registeredUser
                preferencesManager.saveUserSession(
                    userId = registeredUser.id,
                    email = registeredUser.email,
                    name = registeredUser.name,
                    role = registeredUser.role.name
                )
                _authState.value = AuthState.Authenticated(registeredUser)
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Registration failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferencesManager.clearUserSession()
            _currentUser.value = null
            _authState.value = AuthState.Initial
        }
    }

    fun clearError() {
        _authState.value = AuthState.Initial
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModelFactory(
    private val userRepository: UserRepository,
    private val preferencesManager: PreferencesManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(userRepository, preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
