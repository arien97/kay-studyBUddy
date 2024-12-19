package com.example.studybuddy.features.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studybuddy.data.EventsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel(assistedFactory = EventDetailsViewModel.ViewModelFactory::class)
class EventDetailsViewModel @AssistedInject constructor(
    @Assisted private val eventId: String,
    eventsRepository: EventsRepository,
) : ViewModel() {

    val event = eventsRepository.observeEvent(eventId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    @AssistedFactory
    interface ViewModelFactory {
        fun create(eventId: String): EventDetailsViewModel
    }

}