package com.example.digitaldiaryba.ui.components.bottomnavigation


import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.Alignment
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.digitaldiaryba.R
import com.example.digitaldiaryba.ui.navigation.Screen
import com.example.digitaldiaryba.ui.components.topbar.CustomTopBar
import com.example.digitaldiaryba.util.Constants.LOGO_SIZE

@Composable
fun CustomBottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier,
    title: String?,
    showBottomBar: Boolean = true,
    showBackArrow: Boolean = false,
    backgroundColor: Color = MaterialTheme.colorScheme.onSurface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable () -> Unit,
    items: List<BottomNavigationItem> = listOf(
        BottomNavigationItem(
            route = Screen.AlbumFeed.route,
            icon = Icons.Default.Home,
            contentDescription = "Home"
        ),
        BottomNavigationItem(
            route = Screen.NearbyLandmarkScreen.route,
            icon = Icons.Default.AddLocation,
            contentDescription = "NearbyLandmarks"
        ),
        BottomNavigationItem(
            route = Screen.PresentationFeed.route,
            icon = ImageVector.vectorResource(id = R.drawable.ic_magic_wand),
            contentDescription = "Presentations"
        ),
    ),
    showTopbar: Boolean = true,
    snackbarHostState: SnackbarHostState
): Unit {
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
                       },
        topBar = {
            if (showTopbar) {
                CustomTopBar(
                    title = title
                )
            }
        },
        bottomBar = {
            if (showBottomBar && !showBackArrow) {
                BottomAppBar(
                    modifier = Modifier,
                    backgroundColor = backgroundColor,
                    contentColor = contentColor,
                    elevation = 5.dp,
                    cutoutShape = androidx.compose.material.MaterialTheme.shapes.small.copy(
                        CornerSize(percent = 50)
                    )
                ) {
                    BottomNavigation(
                        backgroundColor = MaterialTheme.colorScheme.onSurface,
                    ) {
                        items.forEachIndexed { index, item ->

                                BottomNavigationItem(
                                    icon = item.icon,
                                    unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                                    selectedContentColor = MaterialTheme.colorScheme.onSecondary,
                                    selected = item.route == navController.currentDestination?.route,
                                    onClick = {
                                        if (navController.currentDestination?.route != item.route) {
                                            navController.navigate(item.route)
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .size(LOGO_SIZE)
                                        .weight(0.5f)

                                )

                        }
                    }
                }
            } else if (showBottomBar && showBackArrow) {
                BottomAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = backgroundColor,
                    contentColor = contentColor,
                    elevation = 5.dp,

                ) {
                    BottomNavigation(
                        backgroundColor = MaterialTheme.colorScheme.onSurface,

                    ) {

                            BottomNavigationItem(
                                route = Screen.AlbumFeed.route,
                                icon = Icons.Default.ArrowBack,
                                contentDescription = "Home"
                            ).apply {
                                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                                    BottomNavigationItem(
                                        icon = this.icon,
                                        unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                                        selectedContentColor = MaterialTheme.colorScheme.onSecondary,
                                        selected = this.route == navController.currentDestination?.route,
                                        onClick = {
                                            navController.popBackStack()
                                        },
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .size(LOGO_SIZE)
                                    )
                                }
                                Spacer(Modifier.weight(2.5f, true))
                            }
                    }
                }
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        modifier = modifier
    ) {
//        modifier.padding(it)
//        content()
        innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }


}