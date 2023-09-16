package com.example.digitaldiaryba.ui.album_feed.clean

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.digitaldiaryba.ui.navigation.Screen
import com.example.digitaldiaryba.ui.components.card.album_object_card.AlbumObjectCard
import com.example.digitaldiaryba.ui.saved_presentations.PresentationFeedEvent
import com.example.digitaldiaryba.ui.saved_presentations.PresentationFeedViewModel
import com.example.digitaldiaryba.util.enums.EAlbumCardType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresentationFeed(
    navController: NavController,
    viewModel: PresentationFeedViewModel = hiltViewModel(),
    dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            ),
    ) {
        val state by viewModel.state.collectAsState()
        val context = LocalContext.current

        OutlinedTextField(
            value = state.searchtext,
            onValueChange = {
                viewModel.onEvent(PresentationFeedEvent.Search(it))
            },
            maxLines = 1,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = "Search")
            }
        )
        Spacer(modifier = Modifier.size(height = 30.dp, width = 100.dp))

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LazyRow(

                ) {
                    itemsIndexed(state.presentations) { itemIndex, item ->
                        Spacer(modifier = Modifier.size(20.dp))
                        AlbumObjectCard(
                            cardType = EAlbumCardType.PRESENTATIONS,
                            title = item.name,
                            description = "",
                            onMagicWandIconClick = {},
                            onDeleteIconClick = {
                                viewModel.onEvent(PresentationFeedEvent.DeletePresentation(item))
                            },
                            onCardClick = {
                                navController.navigate(Screen.TabLayout.withStringArgs(item.presentationId.toString(), item.presentationUri, item.subtitlePath.substringAfterLast("/")))
                            },
                            coverPhotoUri = "",
                            presentationCoverPhotoUri = item.coverPhotoUri,
                            context = context
                        )
                    }

                }
            }
        }
    }
}