package com.dev.smartkusina.domain.usecase

import com.dev.smartkusina.domain.repository.FavoritesRepository
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(): List<String> {
        return favoritesRepository.getAllFavorites()
    }
}