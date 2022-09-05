package com.example.flavorsofmiami.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flavorsofmiami.data.local.Datasource
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

    init {
        getRecommendations()
    }

    private fun getRecommendations() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            delay(2000)
            _uiState.update {
                it.copy(
                    recommendations = Datasource.recommendations,
                    loading = false
                )
            }
        }
    }

    // Call this before navigating to the recommendation info screen
    fun setRecommendationInfo(recommendation: Recommendation) {
        _uiState.update { it.copy(recommendation = recommendation) }
    }
}