package com.dev.smartkusina.domain.model

data class SpoonRecipe(
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
    val extendedIngredients: List<ExtendedIngredient>,
    val summary: String,
    val cuisines: List<Any?>,
    val dishTypes: List<String>,
    val diets: List<String>,
    val occasions: List<Any?>,
    val instructions: String,
    val analyzedInstructions: List<AnalyzedInstruction>,
    val originalId: Any?,
    val spoonacularScore: Double,
    val spoonacularSourceUrl: String,
)

data class ExtendedIngredient(
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
    val measures: Measures,
)

data class Measures(
    val us: Us,
    val metric: Metric,
)

data class Us(
    val amount: Double,
    val unitShort: String,
    val unitLong: String,
)

data class Metric(
    val amount: Double,
    val unitShort: String,
    val unitLong: String,
)

data class AnalyzedInstruction(
    val name: String,
    val steps: List<Step>,
)

data class Step(
    val number: Long,
    val step: String,
    val ingredients: List<IngredientRecipe>,
    val equipment: List<Equipment>,
    val length: Length?,
)

data class IngredientRecipe(
    val id: Long,
    val name: String,
    val localizedName: String,
    val image: String,
)

data class Equipment(
    val id: Long,
    val name: String,
    val localizedName: String,
    val image: String,
)

data class Length(
    val number: Long,
    val unit: String,
)
