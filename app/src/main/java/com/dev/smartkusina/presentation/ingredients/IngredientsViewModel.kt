package com.dev.smartkusina.presentation.ingredients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.smartkusina.data.local.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IngredientsUiState(
    val ingredients: List<String> = emptyList(),
    val newIngredient: String = "",
    val isLoading: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class IngredientsViewModel @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(IngredientsUiState())
    val uiState: StateFlow<IngredientsUiState> = _uiState.asStateFlow()

    val ingredients: List<String>
        get() = _uiState.value.ingredients

    init {
        loadIngredients()
    }


    fun refreshIngredients() {
        viewModelScope.launch {
            val ingredients = sharedPreferencesManager.getIngredients()
            _uiState.value = _uiState.value.copy(ingredients = ingredients)
        }
    }
    private fun loadIngredients() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val savedIngredients = sharedPreferencesManager.getIngredients()
                _uiState.value = _uiState.value.copy(
                    ingredients = savedIngredients,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Failed to load ingredients: ${e.message}"
                )
            }
        }
    }

    fun onIngredientChange(value: String) {
        _uiState.value = _uiState.value.copy(newIngredient = value)
    }

    fun addIngredient() {
        val newIngredient = _uiState.value.newIngredient.trim()
        if (newIngredient.isBlank()) {
            _uiState.value = _uiState.value.copy(message = "Please enter an ingredient")
            return
        }

        val currentIngredients = _uiState.value.ingredients
        if (currentIngredients.any { it.equals(newIngredient, ignoreCase = true) }) {
            _uiState.value = _uiState.value.copy(message = "Ingredient already exists")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val updatedList = currentIngredients.toMutableList().apply {
                    add(newIngredient)
                }
                sharedPreferencesManager.saveIngredients(updatedList)
                _uiState.value = _uiState.value.copy(
                    ingredients = updatedList,
                    newIngredient = "",
                    isLoading = false,
                    message = "Ingredient added successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Failed to add ingredient: ${e.message}"
                )
            }
        }
    }

    fun removeIngredient(ingredient: String) {
        viewModelScope.launch {
            try {
                val updatedList = _uiState.value.ingredients.toMutableList().apply {
                    remove(ingredient)
                }
                sharedPreferencesManager.saveIngredients(updatedList)
                _uiState.value = _uiState.value.copy(
                    ingredients = updatedList,
                    message = "Ingredient removed successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to remove ingredient: ${e.message}"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}