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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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

    fun fetchMultipleMealDetails(mealIds: List<String>) {
        viewModelScope.launch {
            try {
                _favoriteMeals.value = Response.Loading()
                Log.d("FavoritesViewModel", "fetchMultipleMealDetails: Starting to fetch ${mealIds.size} meal details")

                val detailsList = mealIds.map { mealId ->
                    async {
                        fetchSingleMealDetail(mealId)
                    }
                }.awaitAll()

                val successfulMeals = detailsList.mapNotNull { response ->
                    when (response) {
                        is Response.Success -> response.data
                        else -> null
                    }
                }

                Log.d("FavoritesViewModel", "fetchMultipleMealDetails: Successfully fetched ${successfulMeals.size} meals out of ${mealIds.size}")
                successfulMeals.forEach { meal ->
                    Log.d("FavoritesViewModel", "Meal: ${meal.name} (ID: ${meal.id})")
                }

                if (successfulMeals.isNotEmpty()) {
                    _favoriteMeals.value = Response.Success(successfulMeals)
                } else {
                }

            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "fetchMultipleMealDetails: General exception: ${e.message}", e)
            }
        }
    }

    private suspend fun fetchSingleMealDetail(mealId: String): Response<MealDetails?> {
        return try {
            Log.d("FavoritesViewModel", "Fetching details for mealId: $mealId")

            val deferred = CompletableDeferred<Response<MealDetails?>>()

            getMealDetailsUseCase(mealId)
                .catch { exception ->
                    Log.e("FavoritesViewModel", "Flow exception for mealId $mealId: ${exception.message}")
                }
                .collect { response ->
                    when (response) {
                        is Response.Success -> {
                            Log.d("FavoritesViewModel", "Successfully fetched mealId: $mealId")
                            deferred.complete(response)
                        }
                        is Response.Error -> {
                            Log.e("FavoritesViewModel", "Error fetching mealId $mealId: ${response.message}")
                            deferred.complete(response)
                        }
                        is Response.Loading -> {
                            Log.d("FavoritesViewModel", "Still loading mealId: $mealId")
                        }
                    }
                }

            deferred.await()

        } catch (e: Exception) {
            Log.e("FavoritesViewModel", "fetchSingleMealDetail: Exception for mealId $mealId: ${e.message}", e)
            Response.Error(message = e.localizedMessage ?: "An error occurred while fetching meal details")
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

                if (favoriteIdsList.isEmpty()) {
                    _favoriteMeals.value = Response.Success(emptyList())
                    return@launch
                }

                fetchMultipleMealDetails(favoriteIdsList)

            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "loadFavorites: Exception occurred: ${e.message}", e)

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
                Log.e("FavoritesViewModel", "toggleFavorite: Exception occurred: ${e.message}", e)
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