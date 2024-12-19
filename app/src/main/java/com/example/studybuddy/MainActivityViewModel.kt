package com.example.studybuddy

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studybuddy.data.AuthRepository
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _isAuthEvent = Channel<Boolean>(Channel.CONFLATED)
    val isAuthEvent get() = _isAuthEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            _isAuthEvent.send(authRepository.isUserAuthenticatedInFirebase())
        }
    }

    fun handleIntent(intent: Intent?) {
        viewModelScope.launch {
            if (!authRepository.isUserAuthenticatedInFirebase()) {
                val pendingDynamicLinkData =
                    FirebaseDynamicLinks.getInstance().getDynamicLink(intent).await()
                if (pendingDynamicLinkData != null) {
                    val link = pendingDynamicLinkData.link
                    if (link != null) {
                        authRepository.verify(link.toString())
                            .onSuccess { _isAuthEvent.send(true) }
                    }
                }
            }
        }
    }
}