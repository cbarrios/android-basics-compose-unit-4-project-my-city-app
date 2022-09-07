package com.example.flavorsofmiami.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.outlined.Church
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.Loyalty
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.flavorsofmiami.R

enum class AppScreen(@StringRes val title: Int) {
    Church(R.string.category_church),
    Wedding(R.string.category_wedding),
    Hotel(R.string.category_hotel),
    Details(R.string.recommendation_details)
}

object NavMenuItems {

    val menuItems = listOf(
        MenuItem(
            AppScreen.Church.name,
            Icons.Outlined.Church,
            Icons.Filled.Church
        ),
        MenuItem(
            AppScreen.Wedding.name,
            Icons.Outlined.Loyalty,
            Icons.Filled.Loyalty
        ),
        MenuItem(
            AppScreen.Hotel.name,
            Icons.Outlined.Hotel,
            Icons.Filled.Hotel
        )
    )
}

data class MenuItem(
    val label: String,
    val icon: ImageVector,
    val iconSelected: ImageVector
)

enum class NavigationType {
    BOTTOM, RAIL, DRAWER
}

enum class ContentType {
    LIST, LIST_DETAIL
}
