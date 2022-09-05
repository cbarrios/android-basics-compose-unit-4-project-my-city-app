package com.example.flavorsofmiami.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.flavorsofmiami.data.model.Recommendation

// (1) Reusable component for each category screen: LazyColumn items filtered by category
@Composable
fun CategoryScreen(
    recommendations: List<Recommendation>,
    modifier: Modifier = Modifier
) {

}

// (2) Recommendation list item: Card (clickable for navigation)
@Composable
fun RecommendationListItem(
    recommendation: Recommendation,
    onClick: (Recommendation) -> Unit,
    modifier: Modifier = Modifier
) {

}