package com.dev.smartkusina.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesManager @Inject constructor(
    private val context: Context
) {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("SmartKusinaPrefs", Context.MODE_PRIVATE)
    }

    private val _ingredientsFlow = MutableStateFlow<List<String>>(emptyList())
    val ingredientsFlow: Flow<List<String>> = _ingredientsFlow.asStateFlow()

    init {
        _ingredientsFlow.value = getIngredients()
    }

    fun saveIngredients(ingredients: List<String>) {
        try {
            sharedPreferences.edit {
                putStringSet(INGREDIENTS_KEY, ingredients.toSet())
            }
            _ingredientsFlow.value = ingredients
        } catch (e: Exception) {
            throw Exception("Failed to save ingredients: ${e.message}")
        }
    }

    fun getIngredients(): List<String> {
        return try {
            sharedPreferences.getStringSet(INGREDIENTS_KEY, emptySet())?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addIngredient(ingredient: String): Boolean {
        return try {
            val currentIngredients = getIngredients().toMutableList()
            if (!currentIngredients.contains(ingredient)) {
                currentIngredients.add(ingredient)
                saveIngredients(currentIngredients)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun removeIngredient(ingredient: String): Boolean {
        return try {
            val currentIngredients = getIngredients().toMutableList()
            val removed = currentIngredients.remove(ingredient)
            if (removed) {
                saveIngredients(currentIngredients)
            }
            removed
        } catch (e: Exception) {
            false
        }
    }

    fun clearAllIngredients() {
        try {
            sharedPreferences.edit {
                remove(INGREDIENTS_KEY)
            }
            _ingredientsFlow.value = emptyList()
        } catch (e: Exception) {
            throw Exception("Failed to clear ingredients: ${e.message}")
        }
    }

    companion object {
        private const val INGREDIENTS_KEY = "user_ingredients"
    }
}