package com.example.digitaldiaryba.ui.navigation

import androidx.annotation.StringRes
import com.example.digitaldiaryba.R


sealed class Screen(val route: String, @StringRes val resourceId: Int) {

    object AlbumDetailScreen : Screen("album_detail_screen", R.string.album_detail_screen)
    object FileDetailScreen : Screen("file_detail_screen", R.string.file_detail_screen)
    object ChooseAlbumNameDialog : Screen("choose_album_name_dialog", R.string.choose_album_name_dialog)
    object ChoosePresentationNameDialog : Screen("choose_presentation_name_dialog", R.string.choose_presentation_name_dialog)
    object EditDescriptionDialog : Screen("choose_description_dialog", R.string.choose_description_dialog)
    object VideoPreviewScreen : Screen("video_prev", R.string.video_prev)
    object VideoCaptureScreen : Screen("video_capture", R.string.video_capture)
    object ImageCaptureScreen : Screen("image_capture", R.string.image_capture)
    object ImagePreviewScreen : Screen("image_prev", R.string.image_prev)
    object PresentationPreviewScreen : Screen("presentation_prev", R.string.presentation_prev)
    object AlbumFeed : Screen("album_feed", R.string.album_feed)
    object PresentationFeed : Screen("presentation_feed", R.string.presentation_feed)
    object AlbumDetails : Screen("album_details", R.string.album_details)
    object NearbyLandmarkScreen : Screen("nearby_landmark_screen", R.string.nearby_landmark_screen)
    object MapScreen : Screen("map_screen", R.string.map_screen)
    object TabLayout : Screen("tab_layout", R.string.tab_layout)

    fun withArgs(vararg args: Int): String {
        return buildString {
            append(route)
            args.forEach{
                append("/$it")
            }
        }
    }

    fun withStringArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach{
                append("/$it")
            }
        }
    }
}


