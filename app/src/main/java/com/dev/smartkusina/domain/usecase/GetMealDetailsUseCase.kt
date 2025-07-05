    package com.dev.smartkusina.domain.usecase

    import com.dev.smartkusina.domain.model.MealDetails
    import com.dev.smartkusina.domain.repository.MealDetailRepository
    import com.dev.smartkusina.util.Response
    import kotlinx.coroutines.flow.Flow
    import javax.inject.Inject

    class GetMealDetailsUseCase @Inject constructor(
        private val repository: MealDetailRepository
    ) {
        suspend operator fun invoke(mealId: String): Flow<Response<MealDetails?>> {
            return repository.getMealById(mealId)
        }
    }