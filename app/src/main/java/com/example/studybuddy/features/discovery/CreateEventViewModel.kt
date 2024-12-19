package com.example.studybuddy.features.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studybuddy.data.EventsRepository
import com.example.studybuddy.domain.EventCreate
import com.example.studybuddy.utils.ToastNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventsRepository: EventsRepository,
    private val toastNotifier: ToastNotifier,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading get() = _isLoading.asStateFlow()

    private val _event = MutableStateFlow(defaultEvent())
    val event = _event.asStateFlow()

    val isValid = event.map { isValid(it) }

    fun edit(edit: EventCreate.() -> EventCreate) {
        _event.update { edit.invoke(it) }
    }

    fun createEvent() {
        viewModelScope.launch {
            val data = _event.value
            if (!isValid(data)) return@launch

            _isLoading.value = true
            eventsRepository.createOrUpdateEvent(data)
                .fold({
                    toastNotifier.showMessage("Created")
                    _event.value = defaultEvent()
                }, {
                    toastNotifier.showMessage(it.message)
                })
            _isLoading.value = false
        }
    }

    private fun isValid(data: EventCreate): Boolean {
        return data.title.isNullOrEmpty().not()
                && data.description.isNullOrEmpty().not()
                && data.location.isNullOrEmpty().not()
                && data.date.isNullOrEmpty().not()
    }

    private fun defaultEvent(): EventCreate {
        return EventCreate(
            title = null,
            description = null,
            location = null,
            course = null,
            date = null,
            startTime = 8f,
            endTime = 20f,
            latitude = 42.3505, // Default CAS location
            longitude = -71.1054 // Default CAS location
        )
    }
}