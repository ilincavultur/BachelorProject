package com.example.digitaldiaryba.ui.album_feed.clean

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.digitaldiaryba.ui.album_feed.AlbumFeedEvent
import com.example.digitaldiaryba.ui.album_feed.AlbumFeedViewModel
import com.example.digitaldiaryba.ui.components.card.album_object_card.AlbumObjectCard
import com.example.digitaldiaryba.util.enums.EAlbumCardType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumFeed(
    navController: NavController,
    viewModel: AlbumFeedViewModel = hiltViewModel(),
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            ),
    ) {
        val state by viewModel.state.collectAsState()
        val mediaNames by viewModel.mediaNames.collectAsState()
        val buildingInfos by viewModel.buildingInfos.collectAsState()
        val context = LocalContext.current

        if(state.isCreatingAlbum) {
            AlbumDialog(state = state, onEvent = viewModel::onEvent)
        }

        if(state.isCreatingPresentation) {
            PresentationDialog(state = state, onEvent = viewModel::onEvent)
        }

        OutlinedTextField(
            value = state.searchText,
            onValueChange = {
                viewModel.onEvent(AlbumFeedEvent.Search(it))
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LazyRow{
                    itemsIndexed(state.albums) { itemIndex, item ->
                        Spacer(modifier = Modifier.size(20.dp))
                        AlbumObjectCard(
                            cardType = EAlbumCardType.ALBUM_FEED,
                            title = item.name,
                            description = item.description ,
                            onMagicWandIconClick = {
                                viewModel.onEvent(AlbumFeedEvent.ShowCreatePresentationDialog(item.albumId))
                            },
                            onDeleteIconClick = {
                                viewModel.onEvent(AlbumFeedEvent.DeleteAlbum(item))
                            },
                            onCardClick = {
                                navController.navigate(Screen.AlbumDetails.withArgs(item.albumId))
                            },
                            coverPhotoUri = item.coverPhotoUri,
                            presentationCoverPhotoUri = "",
                            context = context
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.size(height = 20.dp, width = 100.dp))

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(AlbumFeedEvent.ShowCreateAlbumDialog)
                },
                backgroundColor = androidx.compose.material.MaterialTheme.colors.primary,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create_Album",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}