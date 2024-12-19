package com.example.studybuddy.data

import com.example.studybuddy.domain.User
import com.example.studybuddy.domain.UserStatus
import com.example.studybuddy.utils.suspendRunCatching
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val profileRef = database.getReference("Profiles")

    suspend fun createOrUpdateProfileToFirebase(user: User): Result<Unit> = suspendRunCatching {
        val userUUID = auth.currentUser?.uid.toString()
        val userEmail = auth.currentUser?.email.toString()
        val databaseReference = profileRef.child(userUUID).child("profile")
        val childUpdates = mutableMapOf<String, Any>()
        childUpdates["/profileUUID/"] = userUUID
        childUpdates["/userEmail/"] = userEmail

        if (user.userName != "") childUpdates["/userName/"] = user.userName
        if (user.userProfilePictureUrl != "") childUpdates["/userProfilePictureUrl/"] =
            user.userProfilePictureUrl
        if (user.userSurName != "") childUpdates["/userSurName/"] = user.userSurName
        if (user.userBio != "") childUpdates["/userBio/"] = user.userBio
        if (user.userPhoneNumber != "") childUpdates["/userPhoneNumber/"] = user.userPhoneNumber
        childUpdates["/status/"] = UserStatus.ONLINE.toString()

        databaseReference.updateChildren(childUpdates).await()
    }

    fun observeProfile(): Flow<User?> = callbackFlow {
        val databaseReference = profileRef.child(auth.currentUser?.uid.orEmpty()).child("profile")
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                this@callbackFlow.trySendBlocking(snapshot.getValue(User::class.java))
            }

            override fun onCancelled(error: DatabaseError) = Unit
        }
        databaseReference.addValueEventListener(eventListener)
        awaitClose { databaseReference.removeEventListener(eventListener) }
    }

    suspend fun getProfile(): Result<User?> = suspendRunCatching {
        val databaseReference = profileRef.child(auth.currentUser?.uid.orEmpty()).child("profile")
        databaseReference.get().await().getValue(User::class.java)
    }

    suspend fun updateCourse(course: Set<String>): Result<Unit> = suspendRunCatching {
        val userUUID = auth.currentUser?.uid.toString()
        profileRef.child(userUUID).child("courses")
            .setValue(ArrayList(course))
            .await()
    }

    fun observeCourses(): Flow<Set<String>> = callbackFlow {
        val databaseReference = profileRef.child(auth.currentUser?.uid.orEmpty()).child("courses")
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val values = snapshot.children
                    .mapNotNull { it.getValue(String::class.java) }
                    .toSet()
                this@callbackFlow.trySendBlocking(values)
            }

            override fun onCancelled(error: DatabaseError) = Unit
        }
        databaseReference.addValueEventListener(eventListener)
        awaitClose { databaseReference.removeEventListener(eventListener) }
    }

    suspend fun getCourses(): Result<Set<String>> = suspendRunCatching {
        val databaseReference = profileRef.child(auth.currentUser?.uid.orEmpty()).child("courses")
        databaseReference.get().await().children
            .mapNotNull { it.getValue(String::class.java) }
            .toSet()
    }

    suspend fun updateUserName(userName: String): Result<Unit> = suspendRunCatching {
        val userUUID = auth.currentUser?.uid.toString()
        profileRef.child(userUUID).child("profile")
            .updateChildren(mapOf("userName" to userName))
            .await()
    }

}
