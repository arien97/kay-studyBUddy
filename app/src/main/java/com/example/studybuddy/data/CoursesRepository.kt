package com.example.studybuddy.data

import com.example.studybuddy.domain.Event
import com.example.studybuddy.domain.EventCreate
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
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoursesRepository @Inject constructor() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val eventsDatabaseReference = database.getReference("courses")

    fun observeCourses(): Flow<List<String>> = callbackFlow {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courses = snapshot.children.mapNotNull { it.key }
                this@callbackFlow.trySendBlocking(courses)
            }

            override fun onCancelled(error: DatabaseError) = Unit
        }
        eventsDatabaseReference.addValueEventListener(eventListener)
        awaitClose { eventsDatabaseReference.removeEventListener(eventListener) }
    }

}