package com.example.studybuddy.domain

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

object ProfilePicRepository {

    private val storageRef = FirebaseStorage.getInstance().reference
    private val db = FirebaseFirestore.getInstance()

    fun uploadProfilePicture(userId: String, uri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val profileImagesRef = storageRef.child("profile_images/${UUID.randomUUID()}.jpg")

        profileImagesRef.putFile(uri)
            .addOnSuccessListener {
                profileImagesRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    updateProfilePictureInFirestore(userId, downloadUri.toString(), onSuccess, onFailure)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun updateProfilePictureInFirestore(userId: String, imageUrl: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val userRef = db.collection("users").document(userId)

        userRef.update("profilePicture", imageUrl)
            .addOnSuccessListener {
                onSuccess(imageUrl)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}