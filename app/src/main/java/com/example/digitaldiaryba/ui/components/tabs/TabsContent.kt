package com.example.digitaldiaryba.ui.components.tabs

import androidx.compose.runtime.Composable
import com.example.digitaldiaryba.ui.saved_presentations.PresentationPreviewScreen
import com.example.digitaldiaryba.ui.saved_presentations.PresentationScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState

// START https://www.geeksforgeeks.org/tab-layout-in-android-using-jetpack-compose/
@ExperimentalPagerApi
@Composable
fun TabsContent(
    pagerState: PagerState,
    presentationId: Int,
    presentationUri: String,
    subtitlePath: String
) {
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> PresentationPreviewScreen(presentationUri = presentationUri, subtitlePath = subtitlePath)
            1 -> PresentationScreen(presentationId)
        }
    }
}
// END https://www.geeksforgeeks.org/tab-layout-in-android-using-jetpack-compose/