package com.dev.smartkusina.presentation.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.smartkusina.domain.model.MealDetails
import com.dev.smartkusina.domain.usecase.AddFavoriteUseCase
import com.dev.smartkusina.domain.usecase.GetFavoritesUseCase
import com.dev.smartkusina.domain.usecase.GetMealDetailsUseCase
import com.dev.smartkusina.domain.usecase.IsFavoriteUseCase
import com.dev.smartkusina.domain.usecase.RemoveFavoriteUseCase
import com.dev.smartkusina.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val getMealDetailsUseCase: GetMealDetailsUseCase
) : ViewModel() {

    private val _favoriteMeals = MutableStateFlow<Response<List<MealDetails>>>(Response.Loading())
    val favoriteMeals: StateFlow<Response<List<MealDetails>>> = _favoriteMeals

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                Log.d("FavoritesViewModel", "loadFavorites: Starting to load favorites")
                _favoriteMeals.value = Response.Loading()

                val favoriteIdsList = getFavoritesUseCase()
                Log.d("FavoritesViewModel", "loadFavorites: Retrieved ${favoriteIdsList.size} favorite IDs: $favoriteIdsList")
                _favoriteIds.value = favoriteIdsList.toSet()

                // Get meal details for each favorite ID
                val favoriteMealsList = mutableListOf<MealDetails>()
                for (id in favoriteIdsList) {
                    try {
                        Log.d("FavoritesViewModel", "loadFavorites: Fetching details for meal ID: $id")
                        when (val response = getMealDetailsUseCase(id).first()) {
                            is Response.Success -> {
                                response.data?.let { mealDetails ->
                                    Log.d("FavoritesViewModel", "loadFavorites: Successfully loaded meal: ${mealDetails.name} (ID: ${mealDetails.id})")
                                    favoriteMealsList.add(mealDetails)
                                } ?: run {
                                    Log.w("FavoritesViewModel", "loadFavorites: Meal details is null for ID: $id")
                                }
                            }
                            is Response.Error -> {
                                Log.e("FavoritesViewModel", "loadFavorites: Error loading meal ID $id: ${response.message}")
                            }
                            is Response.Loading -> {
                                Log.d("FavoritesViewModel", "loadFavorites: Still loading meal ID: $id")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("FavoritesViewModel", "loadFavorites: Exception while fetching meal ID $id: ${e.message}", e)
                        continue
                    }
                }

                Log.d("FavoritesViewModel", "loadFavorites: Successfully loaded ${favoriteMealsList.size} meal details out of ${favoriteIdsList.size} favorites")
                _favoriteMeals.value = Response.Success(favoriteMealsList)

            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "loadFavorites: Main exception occurred: ${e.message}", e)

                // Still try to load favorite IDs for toggle functionality
                try {
                    val favoriteIdsList = getFavoritesUseCase()
                    Log.d("FavoritesViewModel", "loadFavorites: Fallback - Retrieved ${favoriteIdsList.size} favorite IDs for toggle functionality")
                    _favoriteIds.value = favoriteIdsList.toSet()
                } catch (idsException: Exception) {
                    Log.e("FavoritesViewModel", "loadFavorites: Failed to load favorite IDs in fallback: ${idsException.message}", idsException)
                    _favoriteIds.value = emptySet()
                }
            }
        }
    }

    fun toggleFavorite(recipeId: String) {
        viewModelScope.launch {
            try {
                val isFavorite = isFavoriteUseCase(recipeId)
                if (isFavorite) {
                    removeFavoriteUseCase(recipeId)
                } else {
                    addFavoriteUseCase(recipeId)
                }

                val currentIds = _favoriteIds.value.toMutableSet()
                if (isFavorite) {
                    currentIds.remove(recipeId)
                } else {
                    currentIds.add(recipeId)
                }
                _favoriteIds.value = currentIds

                loadFavorites()

            } catch (e: Exception) {
                loadFavorites()
            }
        }
    }

    fun isFavorite(recipeId: String): Boolean {
        return _favoriteIds.value.contains(recipeId)
    }

    fun refreshFavorites() {
        loadFavorites()
    }
}