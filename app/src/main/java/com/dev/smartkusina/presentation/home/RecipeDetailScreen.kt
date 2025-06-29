package com.dev.smartkusina.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.dev.smartkusina.domain.model.Meal
import com.dev.smartkusina.domain.model.MealDetails
import com.dev.smartkusina.util.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    mealId: String,
    onNavigateBack: () -> Unit,
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val mealState by viewModel.mealState.collectAsState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(mealId) {
        viewModel.fetchMealDetails(mealId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Recipe Details",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF28C20),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        when (val state = mealState) {
            is Response.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFF28C20))
                }
            }
            is Response.Success -> {
                state.data?.let { meal ->
                    RecipeDetailContent(
                        meal = meal,
                        modifier = Modifier.padding(paddingValues),
                        onYouTubeClick = { url ->
                            uriHandler.openUri(url)
                        },
                        onSourceClick = { url ->
                            uriHandler.openUri(url)
                        }
                    )
                }
            }
            is Response.Error -> {
                ErrorCard(
                    message = state.message ?: "An error occurred",)
            }
        }
    }
}

@Composable
fun RecipeDetailContent(
    meal: MealDetails,
    modifier: Modifier = Modifier,
    onYouTubeClick: (String) -> Unit,
    onSourceClick: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            RecipeHeader(meal = meal)
        }

        item {
            RecipeInfoSection(meal = meal)
        }

        item {
            ActionButtonsSection(
                meal = meal,
                onYouTubeClick = onYouTubeClick,
                onSourceClick = onSourceClick
            )
        }

        item {
            IngredientsSection(meal = meal)
        }

        item {
            InstructionsSection(meal = meal)
        }

        item {
            AdditionalInfoSection(meal = meal)
        }
    }
}

@Composable
fun RecipeHeader(meal: MealDetails) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = meal.imageUrl,
            contentDescription = meal.imageUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Text(
            text = meal.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF28C20)
        )
    }
}

@Composable
fun RecipeInfoSection(meal: MealDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Recipe Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF28C20)
            )

            meal.category?.let {
                InfoRow(label = "Category", value = it)
            }

            meal.area?.let {
                InfoRow(label = "Cuisine", value = it)
            }
        }
    }
}

@Composable
fun ActionButtonsSection(
    meal: MealDetails,
    onYouTubeClick: (String) -> Unit,
    onSourceClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        meal.youtubeUrl?.let { url ->
            if (url.isNotBlank()) {
                Button(
                    onClick = { onYouTubeClick(url) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF0000)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("YouTube")
                }
            }
        }
    }
}

@Composable
fun IngredientsSection(meal: MealDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color(0xFFF28C20),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF28C20)
                )
            }

            meal.ingredients.forEach { ingredient ->
                Text(
                    text = "• ${ingredient.name}: ${ingredient.measure}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }
        }
    }
}


@Composable
fun InstructionsSection(meal: MealDetails) {
    meal.instructions?.let { instructions ->
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = Color(0xFFF28C20),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Instructions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF28C20)
                    )
                }

                val instructionSteps = instructions.split("\r\n", "\n")
                    .filter { it.isNotBlank() }

                instructionSteps.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${index + 1}.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF28C20),
                            modifier = Modifier.width(24.dp)
                        )
                        Text(
                            text = step.trim(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdditionalInfoSection(meal: MealDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Additional Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF28C20)
            )

            InfoRow(label = "Recipe ID", value = meal.id)

        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End
        )
    }
}