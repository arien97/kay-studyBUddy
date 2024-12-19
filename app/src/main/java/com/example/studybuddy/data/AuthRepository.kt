package com.example.studybuddy.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.studybuddy.domain.User
import com.example.studybuddy.utils.suspendRunCatching
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val profileRepository: ProfileRepository,
    @ApplicationContext private val context: Context,
) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("PREFERENCES_APP", Context.MODE_PRIVATE)

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun isUserAuthenticatedInFirebase(): Boolean {
        return auth.currentUser != null //&& auth.currentUser?.isEmailVerified == true
    }

    val currentUserId = auth.currentUser?.uid

    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    suspend fun signIn(email: String, password: String): Result<Unit> = suspendRunCatching {
        withContext(Dispatchers.IO) {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            requireNotNull(result.user)
        }
    }

    suspend fun signInByLink(email: String): Result<Unit> = suspendRunCatching {
        withContext(Dispatchers.IO) {
            val settings = ActionCodeSettings.newBuilder()
                .setUrl("https://studybuddy-59748.firebaseapp.com/finish")
                .setHandleCodeInApp(true)
                .setAndroidPackageName("com.example.studybuddy", true, null)
                .build()
            preferences.edit { putString("email", email) }
            auth.sendSignInLinkToEmail(email, settings).await()
            return@withContext
        }
    }

    suspend fun verify(link: String): Result<Unit> = suspendRunCatching {
        if (!auth.isSignInWithEmailLink(link)) error("not SignIn WithEmailLink")
        val email = preferences.getString("email", null) ?: return@suspendRunCatching
        withContext(Dispatchers.IO) {
            val result = auth.signInWithEmailLink(email, link).await()
            requireNotNull(result.user)
            return@withContext
        }
    }

    suspend fun signUp(email: String, password: String): Result<Unit> = suspendRunCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        requireNotNull(result.user)
        profileRepository.createOrUpdateProfileToFirebase(User())
    }

    suspend fun signOut(): Result<Unit> = suspendRunCatching {
        withContext(Dispatchers.IO) { auth.signOut() }
    }
}