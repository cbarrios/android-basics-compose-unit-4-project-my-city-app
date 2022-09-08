package com.example.flavorsofmiami.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flavorsofmiami.data.local.Datasource
import com.example.flavorsofmiami.data.model.Category
import com.example.flavorsofmiami.data.model.Recommendation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecommendationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RecommendationUiState())
    val uiState: StateFlow<RecommendationUiState> = _uiState.asStateFlow()

    var shouldNavigateToDetails = false
        private set

    fun setupShouldNavigateToDetails() {
        shouldNavigateToDetails = true
    }

    init {
        getRecommendations()
    }

    private fun getRecommendations() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            delay(2000)
            _uiState.update { uiState ->
                val recommendations = Datasource.recommendations
                val churches = recommendations.filter { it.category == Category.CHURCH }
                val weddings = recommendations.filter { it.category == Category.WEDDING }
                val hotels = recommendations.filter { it.category == Category.HOTEL }
                val firstRecommendation = when (uiState.currentScreen) {
                    AppScreen.Church -> churches.firstOrNull()
                    AppScreen.Wedding -> weddings.firstOrNull()
                    AppScreen.Hotel -> hotels.firstOrNull()
                    AppScreen.Details -> uiState.recommendation
                }
                uiState.copy(
                    churches = churches,
                    weddings = weddings,
                    hotels = hotels,
                    recommendation = firstRecommendation,
                    loading = false
                )
            }
        }
    }

    // Call this before navigating to the recommendation info screen
    fun setRecommendationInfo(recommendation: Recommendation) {
        _uiState.update { it.copy(recommendation = recommendation) }
    }

    fun setRecommendationInfoForCurrentScreen(currentScreen: AppScreen) {
        _uiState.update {
            val name = currentScreen.name.lowercase()
            val found = it.recommendation?.category?.name?.lowercase() == name
            val firstRecommendation = when (currentScreen) {
                AppScreen.Church -> if (found) it.recommendation else it.churches.firstOrNull()
                AppScreen.Wedding -> if (found) it.recommendation else it.weddings.firstOrNull()
                AppScreen.Hotel -> if (found) it.recommendation else it.hotels.firstOrNull()
                AppScreen.Details -> it.recommendation
            }
            it.copy(recommendation = firstRecommendation)
        }
    }

    fun setCurrentScreen(title: String, updateFirstRecommendation: Boolean = false) {
        if (updateFirstRecommendation) {
            _uiState.update {
                val currentScreen = AppScreen.valueOf(title)
                val firstRecommendation = when (currentScreen) {
                    AppScreen.Church -> it.churches.firstOrNull()
                    AppScreen.Wedding -> it.weddings.firstOrNull()
                    AppScreen.Hotel -> it.hotels.firstOrNull()
                    AppScreen.Details -> it.recommendation
                }
                it.copy(
                    currentScreen = currentScreen,
                    recommendation = firstRecommendation
                )
            }
        } else {
            _uiState.update { it.copy(currentScreen = AppScreen.valueOf(title)) }
        }
    }
}