package com.example.digitaldiaryba.ui.components.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState

// START https://www.geeksforgeeks.org/tab-layout-in-android-using-jetpack-compose/
@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPagerApi
@Composable
fun TabLayout(
    presentationId: Int,
    presentationUri: String,
    subtitlePath: String
) {
    val pagerState = rememberPagerState(pageCount = 2)

    Column{
        Row{
            Tabs(pagerState = pagerState)
        }
        Row(
            modifier = Modifier.fillMaxHeight()
        ) {
            TabsContent(pagerState = pagerState, presentationId = presentationId, presentationUri = presentationUri, subtitlePath = subtitlePath)
        }
    }
}
// END https://www.geeksforgeeks.org/tab-layout-in-android-using-jetpack-compose/