package com.example.studybuddy.features.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studybuddy.data.AuthRepository
import com.example.studybuddy.data.EventsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    authRepository: AuthRepository,
    eventsRepository: EventsRepository,
) : ViewModel() {

    private val currentUserId = authRepository.currentUserId

    val events = eventsRepository.observeEvents()
        .map { events -> events.filter { it.authorUUID == currentUserId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

}