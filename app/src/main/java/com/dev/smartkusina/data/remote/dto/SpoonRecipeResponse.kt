package com.dev.smartkusina.data.remote.dto

data class SpoonRecipeResponse(
    val recipes: List<SpoonRecipeDto>
)

data class SpoonRecipeDto(
    val id: Long,
    val image: String,
    val imageType: String,
    val title: String,
    val readyInMinutes: Long,
    val servings: Long,
    val sourceUrl: String,
    val vegetarian: Boolean,
    val vegan: Boolean,
    val glutenFree: Boolean,
    val dairyFree: Boolean,
    val veryHealthy: Boolean,
    val cheap: Boolean,
    val veryPopular: Boolean,
    val sustainable: Boolean,
    val lowFodmap: Boolean,
    val weightWatcherSmartPoints: Long,
    val gaps: String,
    val preparationMinutes: Any?,
    val cookingMinutes: Any?,
    val aggregateLikes: Long,
    val healthScore: Double,
    val creditsText: String,
    val license: String,
    val sourceName: String,
    val pricePerServing: Double,
    val extendedIngredients: List<ExtendedIngredientDto>,
    val summary: String,
    val cuisines: List<Any?>,
    val dishTypes: List<String>,
    val diets: List<String>,
    val occasions: List<Any?>,
    val instructions: String,
    val analyzedInstructions: List<AnalyzedInstructionDto>,
    val originalId: Any?,
    val spoonacularScore: Double,
    val spoonacularSourceUrl: String,
)

data class ExtendedIngredientDto(
    val id: Long,
    val aisle: String,
    val image: String,
    val consistency: String,
    val name: String,
    val nameClean: String,
    val original: String,
    val originalName: String,
    val amount: Double,
    val unit: String,
    val meta: List<String>,
    val measures: MeasuresDto,
)

data class MeasuresDto(
    val us: UsDto,
    val metric: MetricDto,
)

data class UsDto(
    val amount: Double,
    val unitShort: String,
    val unitLong: String,
)

data class MetricDto(
    val amount: Double,
    val unitShort: String,
    val unitLong: String,
)

data class AnalyzedInstructionDto(
    val name: String,
    val steps: List<StepDto>,
)

data class StepDto(
    val number: Long,
    val step: String,
    val ingredients: List<IngredientDto>,
    val equipment: List<EquipmentDto>,
    val length: LengthDto?,
)

data class IngredientDto(
    val id: Long,
    val name: String,
    val localizedName: String,
    val image: String,
)

data class EquipmentDto(
    val id: Long,
    val name: String,
    val localizedName: String,
    val image: String,
)

data class LengthDto(
    val number: Long,
    val unit: String,
)
