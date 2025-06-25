package com.dev.smartkusina.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.smartkusina.domain.model.Meal
import com.dev.smartkusina.domain.usecase.GetRandomMealUseCase
import com.dev.smartkusina.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRandomMealUseCase: GetRandomMealUseCase
): ViewModel(){
    private val _mealsState = MutableStateFlow<Response<List<Meal>>>(Response.Loading())
    val mealsState: StateFlow<Response<List<Meal>>> = _mealsState

    fun fetchRandomMeals() {
        viewModelScope.launch {
            getRandomMealUseCase().collect { response ->
                _mealsState.value = response
            }
        }
    }
}