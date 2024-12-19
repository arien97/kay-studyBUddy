package com.example.studybuddy.features.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studybuddy.data.AuthRepository
import com.example.studybuddy.utils.ToastNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val toastNotifier: ToastNotifier,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading get() = _isLoading.asStateFlow()

    private val _authComplete = Channel<Unit>(Channel.CONFLATED)
    val authComplete get() = _authComplete.receiveAsFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.signIn(email, password)
                .fold(
                    { _authComplete.send(Unit) },
                    { toastNotifier.showMessage(it.message) })
            _isLoading.value = false
        }
    }
}