package com.example.flavorsofmiami.ui

import com.example.flavorsofmiami.data.model.Recommendation

data class RecommendationUiState(
    val recommendations: List<Recommendation> = emptyList(),
    val recommendation: Recommendation? = null,
    val loading: Boolean = false
)
