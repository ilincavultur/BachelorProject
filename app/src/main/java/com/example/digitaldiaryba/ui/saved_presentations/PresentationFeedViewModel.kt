package com.example.digitaldiaryba.ui.saved_presentations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitaldiaryba.data.repository.PresentationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PresentationFeedViewModel @Inject constructor(
    private val presentationRepository: PresentationRepository,
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private var presentationList = _searchText.flatMapLatest { input ->
        if (input.isEmpty()) {
            presentationRepository.getAllPresentations()
        } else {
            presentationRepository.searchPresentations(input)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(PresentationFeedState())
    val state = combine(_state, _searchText, presentationList) { state, searchtext, presentations ->
        state.copy(
            presentations = presentations,
            searchtext = searchtext
        )

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PresentationFeedState())

    fun onEvent(event: PresentationFeedEvent) {
        when(event) {
            is PresentationFeedEvent.DeletePresentation -> {
                viewModelScope.launch {
                    presentationRepository.deletePresentation(event.presentation)
                }
            }
            is PresentationFeedEvent.Search -> {
                _searchText.value = event.searchText
                _state.update { it.copy(
                    searchtext = event.searchText
                )
                }
            }
        }
    }
}