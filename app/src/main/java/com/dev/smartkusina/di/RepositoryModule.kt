package com.dev.smartkusina.di
import com.dev.smartkusina.BuildConfig
import com.dev.smartkusina.data.local.dao.FavoritesDao
import com.dev.smartkusina.data.local.dao.UserDao
import com.dev.smartkusina.data.remote.dummyjson.DummyjsonService
import com.dev.smartkusina.data.remote.spoonacular.SpoonService
import com.dev.smartkusina.data.remote.the_meal.DetailMealService
import com.dev.smartkusina.data.remote.the_meal.RandomMealService
import com.dev.smartkusina.data.repository.FavoritesRepositoryImpl
import com.dev.smartkusina.data.repository.GetAllRecipesFromDummyJsonImpl
import com.dev.smartkusina.data.repository.GetMealDetailRepositoryImpl
import com.dev.smartkusina.data.repository.GetRandomMealRepositoryImpl
import com.dev.smartkusina.data.repository.GetRandomSpoonRecipeImpl
import com.dev.smartkusina.data.repository.GetSimilarSpoonRecipeImpl
import com.dev.smartkusina.data.repository.UserRepositoryImpl
import com.dev.smartkusina.domain.repository.FavoritesRepository
import com.dev.smartkusina.domain.repository.MealDetailRepository
import com.dev.smartkusina.domain.repository.MealRepository
import com.dev.smartkusina.domain.repository.RecipeRepository
import com.dev.smartkusina.domain.repository.SimilarSpoonRecipeRepository
import com.dev.smartkusina.domain.repository.SpoonRecipeRepository
import com.dev.smartkusina.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepositoryImpl(userDao)
    }

    @Provides
    @Singleton
    fun provideMealRepository(@ThemeAlDBRetrofit randomMealService: RandomMealService): MealRepository {
        return GetRandomMealRepositoryImpl(randomMealService)
    }

    @Provides
    @Singleton
    fun provideMealDetailRepository(@ThemeAlDBRetrofit detailMealService: DetailMealService): MealDetailRepository {
        return GetMealDetailRepositoryImpl(detailMealService)
    }

    @Provides
    @Singleton
    fun provideFavoritesRepository(favoritesDao: FavoritesDao): FavoritesRepository {
        return FavoritesRepositoryImpl(favoritesDao)
    }

    @Provides
    @Singleton
    fun provideDummyJsonRepository(@DummyJsonRetrofit dummyjsonService: DummyjsonService): RecipeRepository {
        return GetAllRecipesFromDummyJsonImpl(dummyjsonService)
    }

    @Provides
    @Singleton
    fun provideSpoonRecipeRepository(@SpoonacularRetrofit spoonService: SpoonService): SpoonRecipeRepository {
        return GetRandomSpoonRecipeImpl(spoonService)
    }

    @Provides
    @Singleton
    fun provideSimilarSpoonRecipeRepository(
        @SpoonacularRetrofit spoonService: SpoonService
    ): SimilarSpoonRecipeRepository {
        val apiKey = BuildConfig.SPOONACULAR_API_KEY
        return GetSimilarSpoonRecipeImpl(spoonService, apiKey)
    }
}