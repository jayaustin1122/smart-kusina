package com.dev.smartkusina.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.smartkusina.domain.model.Meal
import com.dev.smartkusina.domain.model.MealDetails
import com.dev.smartkusina.domain.model.Recipes
import com.dev.smartkusina.domain.usecase.GetAllRecipesUseCase
import com.dev.smartkusina.domain.usecase.GetMealDetailsUseCase
import com.dev.smartkusina.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val useCase: GetMealDetailsUseCase,
    private val getAllRecipesUseCase: GetAllRecipesUseCase
) : ViewModel() {

    private val _mealState = MutableStateFlow<Response<MealDetails?>>(Response.Loading())
    val mealState: StateFlow<Response<MealDetails?>> = _mealState.asStateFlow()

    private val _recipesState = MutableStateFlow<Response<List<Recipes>>>(Response.Loading())
    val recipesState: StateFlow<Response<List<Recipes>>> = _recipesState.asStateFlow()

    fun fetchMealDetails(mealId: String) {
        viewModelScope.launch {
            useCase(mealId).collect { response ->
                _mealState.value = response
                if (response is Response.Error) {

                    Log.e("Error", "fetchMealDetails: ${response.message}")
                } else if (response is Response.Success) {
                    Log.d("Response", "fetchMealDetails:  ${response.data}")
                } else if (response is Response.Loading) {
                    Log.d("Response", "fetchMealDetails: Loading")
                }
            }
        }
    }

    fun fetchAllRecipes() {
        viewModelScope.launch {
            getAllRecipesUseCase().collect { response ->
                _recipesState.value = response
                if (response is Response.Error) {
                    Log.e("Error", "fetchAllRecipes: ${response.message}")
                } else if (response is Response.Success) {
                    Log.d("Response", "fetchAllRecipes:  ${response.data}")
                } else if (response is Response.Loading) {
                    Log.d("Response", "fetchAllRecipes: Loading")
                }
            }
        }
    }
}