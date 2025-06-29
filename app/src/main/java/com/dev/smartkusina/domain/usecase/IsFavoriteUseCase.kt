package com.dev.smartkusina.domain.usecase

import com.dev.smartkusina.domain.repository.FavoritesRepository
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(recipeId: String): Boolean {
        return favoritesRepository.isFavorite(recipeId)
    }
}