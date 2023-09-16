package com.example.digitaldiaryba.ui.components.bottomnavigation

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val route: String,
    val icon: ImageVector? =  null,
    val contentDescription: String? = null,
)