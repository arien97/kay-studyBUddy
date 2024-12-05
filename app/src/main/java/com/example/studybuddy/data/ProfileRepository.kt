package com.example.studybuddy.data

import com.example.studybuddy.domain.User
import com.example.studybuddy.domain.UserStatus
import com.example.studybuddy.utils.suspendRunCatching
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()


    suspend fun createOrUpdateProfileToFirebase(user: User): Result<Unit> = suspendRunCatching {
        val userUUID = auth.currentUser?.uid.toString()
        val userEmail = auth.currentUser?.email.toString()
        val databaseReference =
            database.getReference("Profiles").child(userUUID).child("profile")
        val childUpdates = mutableMapOf<String, Any>()
        childUpdates["/profileUUID/"] = userUUID
        childUpdates["/userEmail/"] = userEmail

        if (user.userName != "") childUpdates["/userName/"] = user.userName
        if (user.userProfilePictureUrl != "") childUpdates["/userProfilePictureUrl/"] =
            user.userProfilePictureUrl
        if (user.userSurName != "") childUpdates["/userSurName/"] = user.userSurName
        if (user.userBio != "") childUpdates["/userBio/"] = user.userBio
        if (user.userPhoneNumber != "") childUpdates["/userPhoneNumber/"] =
            user.userPhoneNumber
        childUpdates["/status/"] = UserStatus.ONLINE.toString()

        databaseReference.updateChildren(childUpdates).await()
    }

    suspend fun loadProfileFromFirebase(): Result<User> = suspendRunCatching {
        val userUUID = auth.currentUser?.uid
        val databaseReference = database.getReference("Profiles")
        val user = databaseReference.get().await()
            .child(userUUID!!).child("profile")
            .getValue(User::class.java) ?: User()
        return@suspendRunCatching user
    }

}