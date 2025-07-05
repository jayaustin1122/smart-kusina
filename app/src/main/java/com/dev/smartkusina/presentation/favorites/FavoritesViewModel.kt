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

    private val _mealDetailsList = MutableStateFlow<Response<List<MealDetails>>>(Response.Loading())
    val mealDetailsList: StateFlow<Response<List<MealDetails>>> = _mealDetailsList

    init {
        loadFavorites()
    }

    fun fetchMultipleMealDetails(mealIds: List<String>) {
        viewModelScope.launch {
            try {
                _mealDetailsList.value = Response.Loading()
                val detailsList = mutableListOf<MealDetails>()

                for (mealId in mealIds) {
                    try {
                        val response = getMealDetailsUseCase(mealId).first()
                        when (response) {
                            is Response.Success -> {
                                response.data?.let {
                                    detailsList.add(it)
                                } ?: Log.w("FavoritesViewModel", "Null data for mealId: $mealId")
                            }
                            is Response.Error -> {
                                Log.e("FavoritesViewModel", "Error fetching mealId $mealId: ${response.message}")
                            }
                            else -> {
                                Log.d("FavoritesViewModel", "Still loading mealId: $mealId")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("FavoritesViewModel", "Exception for mealId $mealId: ${e.message}")
                    }
                }

                Log.d("FavoritesViewModel", "fetchMultipleMealDetails: Successfully fetched ${detailsList.size} meals")
                detailsList.forEach { meal ->
                    Log.d("FavoritesViewModel", "Meal: ${meal.name} (ID: ${meal.id})")
                }

                _mealDetailsList.value = Response.Success(detailsList)
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "General exception: ${e.message}", e)
            }
        }
    }


    fun loadFavorites() {
        viewModelScope.launch {
            try {
                Log.d("FavoritesViewModel", "loadFavorites: Starting to load favorites")
                _favoriteMeals.value = Response.Loading()

                val favoriteIdsList = getFavoritesUseCase()
                Log.d("FavoritesViewModel", "loadFavorites: Retrieved ${favoriteIdsList.size} favorite IDs: $favoriteIdsList")
                _favoriteIds.value = favoriteIdsList.toSet()

                fetchMultipleMealDetails(favoriteIdsList)

                val detailsResult = _mealDetailsList.value
                _favoriteMeals.value = detailsResult

            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "loadFavorites: Main exception occurred: ${e.message}", e)

                try {
                    val fallbackIds = getFavoritesUseCase()
                    _favoriteIds.value = fallbackIds.toSet()
                } catch (idsException: Exception) {
                    Log.e("FavoritesViewModel", "loadFavorites: Fallback failed to load favorite IDs: ${idsException.message}", idsException)
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