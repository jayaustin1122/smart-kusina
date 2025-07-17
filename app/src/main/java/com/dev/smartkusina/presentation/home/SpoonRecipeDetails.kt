package com.dev.smartkusina.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dev.smartkusina.R
import com.dev.smartkusina.domain.model.SpoonRecipe
import com.dev.smartkusina.domain.model.ExtendedIngredient
import com.dev.smartkusina.domain.model.AnalyzedInstruction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpoonRecipeDetails(
    onNavigateBack: () -> Unit,
    recipe: SpoonRecipe
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Ingredients", "Instructions")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        TopAppBar(
            title = {

            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFF28C20),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White,
                actionIconContentColor = Color.White
            )
        )


        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AsyncImage(
                    model = recipe.image,
                    contentDescription = recipe.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            item {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RecipeStatItem(
                        icon = painterResource(R.drawable.baseline_access_time_24),
                        value = "${recipe.readyInMinutes}",
                        label = "Minutes"
                    )
                    RecipeStatItem(
                        icon = painterResource(R.drawable.baseline_room_service_24),
                        value = "${recipe.servings}",
                        label = "Servings"
                    )
                    RecipeStatItem(
                        icon = painterResource(R.drawable.baseline_health_and_safety_24),
                        value = "${recipe.healthScore.toInt()}",
                        label = "Health Score"
                    )
                    RecipeStatItem(
                        icon = painterResource(R.drawable.baseline_handshake_24),
                        value = "${recipe.aggregateLikes}",
                        label = "Likes"
                    )
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    val tags = mutableListOf<String>()
                    if (recipe.vegetarian) tags.add("Vegetarian")
                    if (recipe.vegan) tags.add("Vegan")
                    if (recipe.glutenFree) tags.add("Gluten Free")
                    if (recipe.dairyFree) tags.add("Dairy Free")
                    if (recipe.veryHealthy) tags.add("Very Healthy")
                    if (recipe.cheap) tags.add("Budget Friendly")
                    if (recipe.veryPopular) tags.add("Popular")
                    if (recipe.sustainable) tags.add("Sustainable")

                    items(tags) { tag ->
                        DietTag(tag = tag)
                    }
                }
            }

            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFFF28C20),
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFFF28C20)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = selectedTab == index,
                            onClick = { selectedTab = index }
                        )
                    }
                }
            }

            when (selectedTab) {
                0 -> {
                    item {
                        OverviewContent(recipe = recipe)
                    }
                }
                1 -> {
                    items(recipe.extendedIngredients) { ingredient ->
                        IngredientItem(ingredient = ingredient)
                    }
                }
                2 -> {
                    if (recipe.analyzedInstructions.isNotEmpty()) {
                        items(recipe.analyzedInstructions) { instruction ->
                            InstructionSection(instruction = instruction)
                        }
                    } else {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Text(
                                    text = recipe.instructions.ifEmpty { "No instructions available" },
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeStatItem(
    icon: Painter,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            tint = Color(0xFFF28C20),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFF333333)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF666666)
        )
    }
}

@Composable
fun DietTag(tag: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF28C20)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = tag,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun OverviewContent(recipe: SpoonRecipe) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF28C20)
            )

            // Remove HTML tags from summary
            val cleanSummary = recipe.summary
                .replace(Regex("<[^>]*>"), "")
                .replace("&quot;", "\"")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")

            Text(
                text = cleanSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )

            Divider(color = Color(0xFFE0E0E0))

            if (recipe.dishTypes.isNotEmpty()) {
                Text(
                    text = "Dish Types",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF28C20)
                )
                Text(
                    text = recipe.dishTypes.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
            }

            if (recipe.diets.isNotEmpty()) {
                Text(
                    text = "Diets",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF28C20)
                )
                Text(
                    text = recipe.diets.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
            }

            Text(
                text = "Price per serving: $${String.format("%.2f", recipe.pricePerServing / 100)}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
fun IngredientItem(ingredient: ExtendedIngredient) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://spoonacular.com/cdn/ingredients_100x100/${ingredient.image}",
                contentDescription = ingredient.name,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
                Text(
                    text = ingredient.original,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }

            Text(
                text = "${ingredient.amount} ${ingredient.unit}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFF28C20)
            )
        }
    }
}

@Composable
fun InstructionSection(instruction: AnalyzedInstruction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (instruction.name.isNotEmpty()) {
                Text(
                    text = instruction.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF28C20)
                )
            }

            instruction.steps.forEach { step ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Card(
                        shape = RoundedCornerShape(50),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF28C20))
                    ) {
                        Text(
                            text = step.number.toString(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = step.step,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF333333)
                    )
                }

                if (step != instruction.steps.last()) {
                    Divider(color = Color(0xFFE0E0E0))
                }
            }
        }
    }
}