package com.example.flavorsofmiami.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.outlined.Church
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.Loyalty
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.flavorsofmiami.AppScreen

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
