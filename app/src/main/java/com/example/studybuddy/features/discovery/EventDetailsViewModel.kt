package com.example.studybuddy.features.discovery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studybuddy.data.EventsRepository
import com.example.studybuddy.domain.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val eventsRepository: EventsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = checkNotNull(savedStateHandle["eventId"])

    private val _isAddedToCalendar = MutableStateFlow(false)
    val isAddedToCalendar = _isAddedToCalendar.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    val event: StateFlow<Event?> = eventsRepository.observeEvent(eventId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        checkCalendarStatus()
    }

    private fun checkCalendarStatus() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val isInCalendar = eventsRepository.isEventInCalendar(eventId)
                _isAddedToCalendar.value = isInCalendar
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to check calendar status"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleCalendarStatus() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                if (_isAddedToCalendar.value) {
                    eventsRepository.removeEventFromCalendar(eventId)
                        .onSuccess { _isAddedToCalendar.value = false }
                        .onFailure { throw it }
                } else {
                    eventsRepository.addEventToCalendar(eventId)
                        .onSuccess { _isAddedToCalendar.value = true }
                        .onFailure { throw it }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update calendar status"
            } finally {
                _isLoading.value = false
            }
        }
    }
}