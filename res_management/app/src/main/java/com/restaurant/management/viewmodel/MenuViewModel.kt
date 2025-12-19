package com.restaurant.management.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.restaurant.management.data.model.MenuItem
import com.restaurant.management.data.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MenuViewModel(private val menuRepository: MenuRepository) : ViewModel() {

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _menuState = MutableStateFlow<MenuState>(MenuState.Initial)
    val menuState: StateFlow<MenuState> = _menuState.asStateFlow()

    init {
        loadMenuItems()
        loadCategories()
    }

    private fun loadMenuItems() {
        viewModelScope.launch {
            menuRepository.getAllMenuItems().collect { items ->
                _menuItems.value = items
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            menuRepository.getAllCategories().collect { cats ->
                _categories.value = cats
            }
        }
    }

    fun loadMenuItemsByCategory(category: String?) {
        _selectedCategory.value = category
        viewModelScope.launch {
            if (category == null) {
                menuRepository.getAllMenuItems().collect { items ->
                    _menuItems.value = items
                }
            } else {
                menuRepository.getMenuItemsByCategory(category).collect { items ->
                    _menuItems.value = items
                }
            }
        }
    }

    fun addMenuItem(menuItem: MenuItem) {
        viewModelScope.launch {
            _menuState.value = MenuState.Loading
            val result = menuRepository.insertMenuItem(menuItem)
            
            result.onSuccess {
                _menuState.value = MenuState.Success("Menu item added successfully")
            }.onFailure { error ->
                _menuState.value = MenuState.Error(error.message ?: "Failed to add menu item")
            }
        }
    }

    fun updateMenuItem(menuItem: MenuItem) {
        viewModelScope.launch {
            _menuState.value = MenuState.Loading
            val result = menuRepository.updateMenuItem(menuItem)
            
            result.onSuccess {
                _menuState.value = MenuState.Success("Menu item updated successfully")
            }.onFailure { error ->
                _menuState.value = MenuState.Error(error.message ?: "Failed to update menu item")
            }
        }
    }

    fun deleteMenuItem(menuItem: MenuItem) {
        viewModelScope.launch {
            _menuState.value = MenuState.Loading
            val result = menuRepository.deleteMenuItem(menuItem)
            
            result.onSuccess {
                _menuState.value = MenuState.Success("Menu item deleted successfully")
            }.onFailure { error ->
                _menuState.value = MenuState.Error(error.message ?: "Failed to delete menu item")
            }
        }
    }

    fun clearState() {
        _menuState.value = MenuState.Initial
    }
}

sealed class MenuState {
    object Initial : MenuState()
    object Loading : MenuState()
    data class Success(val message: String) : MenuState()
    data class Error(val message: String) : MenuState()
}

class MenuViewModelFactory(private val menuRepository: MenuRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(menuRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
