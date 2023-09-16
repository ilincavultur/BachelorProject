package com.example.digitaldiaryba.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.digitaldiaryba.ui.file_detail.FileDetailScreen
import com.example.digitaldiaryba.ui.album_detail.add_image.ImagePreviewScreen
import com.example.digitaldiaryba.util.ImageCaptureScreen
import com.example.digitaldiaryba.ui.album_detail.add_video.VideoCaptureScreen
import com.example.digitaldiaryba.ui.album_detail.add_video.VideoPreviewScreen
import com.example.digitaldiaryba.ui.album_feed.clean.*
import com.example.digitaldiaryba.ui.components.tabs.TabLayout
import com.example.digitaldiaryba.ui.map.MapScreen
import com.example.digitaldiaryba.ui.nearby_landmark.NearbyLandmarkScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalPagerApi::class, ExperimentalCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Navigation(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier,
    snackbarHostState: SnackbarHostState
) {
    //val snackbarHostState = rememberScaffoldState()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize()
    ) {
        composable(Screen.AlbumFeed.route) {
            AlbumFeed(navController = navController)
        }
        composable(Screen.PresentationFeed.route) {
            PresentationFeed(navController = navController)
        }
        composable(
            Screen.AlbumDetails.route + "/{albumId}",
            arguments = listOf(navArgument("albumId") {
                type = NavType.IntType
                nullable = false
            }
        )) {
            AlbumDetails(navController = navController, albumId = it.arguments?.getInt("albumId"))
        }
        composable(
            Screen.FileDetailScreen.route + "/{mediaId}",
            arguments = listOf(navArgument("mediaId") {
                type = NavType.IntType
                nullable = false
            })
        ) {
            FileDetailScreen(navController = navController, mediaId = it.arguments?.getInt("mediaId"), snackbarHostState = snackbarHostState)
        }
        composable(
            Screen.VideoCaptureScreen.route + "/{albumId}",
            arguments = listOf(navArgument("albumId") {
                type = NavType.IntType
                nullable = false
            }
            )
        ) {
            VideoCaptureScreen(navController = navController, albumId =  it.arguments?.getInt("albumId"))
        }

        composable(
            Screen.ImageCaptureScreen.route + "/{albumId}",
            arguments = listOf(navArgument("albumId") {
                type = NavType.IntType
                nullable = false
            }
            )
        ) {
            ImageCaptureScreen(navController = navController, albumId =  it.arguments?.getInt("albumId"))
        }

        composable(
            Screen.VideoPreviewScreen.route + "/{uri}",
            arguments = listOf(navArgument("uri") {
                type = NavType.StringType
                nullable = false
            })
        ) {
            val uri = it.arguments?.getString("uri") ?: ""
            VideoPreviewScreen(uri = uri)
        }

        composable(
            Screen.ImagePreviewScreen.route + "/{uri}",
            arguments = listOf(navArgument("uri") {
                type = NavType.StringType
                nullable = false
            })
        ) {
            val uri = it.arguments?.getString("uri") ?: ""
            ImagePreviewScreen(uri = uri)
        }

        composable(
            Screen.TabLayout.route + "/{presentationId}/{presentationUri}/{subtitlePath}",
            arguments = listOf(
                navArgument("presentationId") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("presentationUri") {
                type = NavType.StringType
                nullable = false
                },
                navArgument("subtitlePath") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            val presentationId = it.arguments?.getString("presentationId") ?: ""
            val presentationUri = it.arguments?.getString("presentationUri") ?: ""
            val subtitlePath = it.arguments?.getString("subtitlePath") ?: ""
            TabLayout(presentationId = presentationId.toInt(), presentationUri = presentationUri, subtitlePath = subtitlePath)
        }

        composable(Screen.NearbyLandmarkScreen.route) {
            NearbyLandmarkScreen(navController = navController, snackbarHostState = snackbarHostState)
        }

        composable(
            Screen.MapScreen.route + "/{latitude}/{longitude}/{landmarkName}",
            arguments = listOf(
                navArgument("latitude") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("longitude") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("landmarkName") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            val latitude = it.arguments?.getString("latitude") ?: ""
            val longitude = it.arguments?.getString("longitude") ?: ""
            val landmarkName = it.arguments?.getString("landmarkName") ?: ""
            MapScreen(latitude = latitude, longitude = longitude, landmarkName = landmarkName, snackbarHostState = snackbarHostState)
        }
    }
}