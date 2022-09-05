package com.example.flavorsofmiami.data.model

import androidx.annotation.StringRes

data class Recommendation(
    @StringRes val title: Int,
    @StringRes val description: Int,
    val category: Category
)

enum class Category {
    CHURCH, WEDDING, HOTEL
}
