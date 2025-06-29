package com.dev.smartkusina.domain.usecase

import com.dev.smartkusina.domain.repository.FavoritesRepository
import javax.inject.Inject

class AddFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(recipeId: String) {
        favoritesRepository.addFavorite(recipeId)
    }
}