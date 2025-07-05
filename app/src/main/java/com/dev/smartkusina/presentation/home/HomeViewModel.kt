package com.dev.smartkusina.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.smartkusina.domain.model.Meal
import com.dev.smartkusina.domain.model.SpoonRecipe
import com.dev.smartkusina.domain.usecase.GetRandomMealUseCase
import com.dev.smartkusina.domain.usecase.GetRandomSpoonRecipeUseCase
import com.dev.smartkusina.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRandomMealUseCase: GetRandomMealUseCase,
    private val getRandomSpoonRecipeUseCase: GetRandomSpoonRecipeUseCase
): ViewModel(){
    private val _mealsState = MutableStateFlow<Response<List<Meal>>>(Response.Loading())
    val mealsState: StateFlow<Response<List<Meal>>> = _mealsState

    private val _spoonRecipesState = MutableStateFlow<Response<List<SpoonRecipe>>>(Response.Loading())
    val spoonRecipesState: StateFlow<Response<List<SpoonRecipe>>> = _spoonRecipesState

    fun fetchRandomMeals() {
        viewModelScope.launch {
            getRandomMealUseCase().collect { response ->
                _mealsState.value = response
            }
        }
    }

    fun fetchRandomSpoonRecipes() {
        viewModelScope.launch {
            getRandomSpoonRecipeUseCase().collect { response ->
                _spoonRecipesState.value = response
            }
        }
    }
}