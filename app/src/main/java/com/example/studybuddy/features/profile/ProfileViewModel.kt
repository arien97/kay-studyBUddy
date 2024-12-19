package com.example.studybuddy.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studybuddy.data.AuthRepository
import com.example.studybuddy.data.EventsRepository
import com.example.studybuddy.data.ProfileRepository
import com.example.studybuddy.utils.ToastNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val eventsRepository: EventsRepository,
    private val toastNotifier: ToastNotifier,
) : ViewModel() {

    val events = eventsRepository.observePersonalEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading get() = _isLoading.asStateFlow()

    private val _signOut = Channel<Unit>(Channel.CONFLATED)
    val signOut get() = _signOut.receiveAsFlow()

    private val _username = MutableStateFlow("Username")
    val username: StateFlow<String> = _username.asStateFlow()

    init {
        viewModelScope.launch {
            val username = profileRepository.getProfile().getOrNull()?.userName
            if (username.isNullOrEmpty().not()) {
                _username.value = username.orEmpty()
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.signOut()
                .fold({ _signOut.send(Unit) }, { toastNotifier.showMessage(it.message) })
            _isLoading.value = false
        }
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            _username.value = newUsername
            profileRepository.updateUserName(userName = newUsername)
        }
    }
}
