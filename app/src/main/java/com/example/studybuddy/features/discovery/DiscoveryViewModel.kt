package com.example.studybuddy.features.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studybuddy.data.EventsRepository
import com.example.studybuddy.data.ProfileRepository
import com.example.studybuddy.domain.EventCreate
import com.example.studybuddy.utils.ToastNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    eventsRepository: EventsRepository,
    profileRepository: ProfileRepository,
) : ViewModel() {

    val courses = profileRepository.observeCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())
    val events = eventsRepository.observeEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

}