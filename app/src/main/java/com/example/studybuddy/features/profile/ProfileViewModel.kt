package com.example.studybuddy.features.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studybuddy.data.AuthRepository
import com.example.studybuddy.data.EventsRepository
import com.example.studybuddy.data.ProfileRepository
//import com.example.studybuddy.domain.ProfilePicRepository
import com.example.studybuddy.utils.ToastNotifier
import com.google.firebase.storage.FirebaseStorage
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
open class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val eventsRepository: EventsRepository,
    private val toastNotifier: ToastNotifier,
) : ViewModel() {

    private val currentUserId = authRepository.currentUserId

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail

    init {
        fetchUserEmail()
    }

    private fun fetchUserEmail() {
        viewModelScope.launch {
            val email = authRepository.getCurrentUserEmail()
            _userEmail.value = email ?: "No email found"
        }
    }

    val events = eventsRepository.observeEvents()
        .map { events -> events.filter { it.authorUUID == currentUserId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading get() = _isLoading.asStateFlow()

    private val _signOut = Channel<Unit>(Channel.CONFLATED)
    val signOut get() = _signOut.receiveAsFlow()

    private val _username = MutableStateFlow("Username")
    open val username: StateFlow<String> = _username.asStateFlow()

    private val _profilePictureUrl = MutableStateFlow<String?>(null)
    val profilePictureUrl: StateFlow<String?> = _profilePictureUrl.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = profileRepository.getProfile().getOrNull()
            profile?.let {
                if (it.userName.isNotEmpty()) {
                    _username.value = it.userName
                }
                _profilePictureUrl.value = it.userProfilePictureUrl
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

    open fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            _username.value = newUsername
            profileRepository.updateUserName(userName = newUsername)
        }
    }

    fun uploadProfilePicture(userId: String, uri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val profileImagesRef = storageRef.child("profile_images/$userId.jpg")

        println("Starting upload to: ${profileImagesRef.path}")
        println("URI: $uri")

        profileImagesRef.putFile(uri)
            .addOnSuccessListener {
                println("Upload successful, fetching download URL...")
                profileImagesRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    println("Download URL retrieved: $downloadUri")
                    onSuccess(downloadUri.toString())
                }.addOnFailureListener { exception ->
                    println("Error fetching download URL: ${exception.message}")
                    onFailure(exception)
                }
            }
            .addOnFailureListener { exception ->
                println("Upload failed: ${exception.message}")
                onFailure(exception)
            }
    }

}