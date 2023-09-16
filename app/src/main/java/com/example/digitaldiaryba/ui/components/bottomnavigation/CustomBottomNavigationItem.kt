package com.example.digitaldiaryba.ui.components.bottomnavigation

import androidx.compose.foundation.layout.*

import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.digitaldiaryba.util.Constants
import com.example.digitaldiaryba.util.Constants.LOGO_SIZE

@Composable
fun RowScope.BottomNavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectedContentColor: Color = MaterialTheme.colors.onSecondary,
    unselectedContentColor: Color = MaterialTheme.colors.onBackground,
): Unit {

    BottomNavigationItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Box(
                modifier = modifier

            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "",
                        modifier = modifier.align(Alignment.Center)
                    )
                }
            }
        },
        enabled = enabled,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,

    )



}