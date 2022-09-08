package com.example.flavorsofmiami.ui

import com.example.flavorsofmiami.data.model.Recommendation

data class RecommendationUiState(
    val churches: List<Recommendation> = emptyList(),
    val weddings: List<Recommendation> = emptyList(),
    val hotels: List<Recommendation> = emptyList(),
    val recommendation: Recommendation? = null,
    val currentScreen: AppScreen = AppScreen.Church,
    val loading: Boolean = false
)
