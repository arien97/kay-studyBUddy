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
class EventsRepository @Inject constructor() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val eventsDatabaseReference = database.getReference("events")

    suspend fun createOrUpdateEvent(
        eventCreate: EventCreate
    ): Result<Unit> = suspendRunCatching {
        val childUpdates = mutableMapOf<String, Any?>()

        childUpdates["title"] = eventCreate.title
        childUpdates["description"] = eventCreate.description
        childUpdates["course"] = eventCreate.course
        childUpdates["date"] = eventCreate.date
        childUpdates["startTime"] = eventCreate.startTime
        childUpdates["endTime"] = eventCreate.endTime
        childUpdates["authorUsername"] = auth.currentUser?.email
        childUpdates["authorUUID"] = auth.currentUser?.uid
        childUpdates["latitude"] = eventCreate.latitude
        childUpdates["longitude"] = eventCreate.longitude
        childUpdates["location"] = eventCreate.location

        eventsDatabaseReference
            .child(UUID.randomUUID().toString())
            .updateChildren(childUpdates)
            .await()
    }


    fun observeEvents(): Flow<List<Event>> = callbackFlow {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = snapshot.children.mapNotNull {
                    val event = it.getValue(Event::class.java) ?: return@mapNotNull null
                    val id = it.key ?: return@mapNotNull null
                    return@mapNotNull event.copy(id = id)
                }
                this@callbackFlow.trySendBlocking(events)
            }

            override fun onCancelled(error: DatabaseError) = Unit
        }
        eventsDatabaseReference.addValueEventListener(eventListener)
        awaitClose { eventsDatabaseReference.removeEventListener(eventListener) }
    }

    fun observeEvent(id: String): Flow<Event?> = callbackFlow {
        val eventRef = eventsDatabaseReference.child(id)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val event = snapshot.getValue(Event::class.java)
                this@callbackFlow.trySendBlocking(event?.copy(id = id))
            }

            override fun onCancelled(error: DatabaseError) = Unit
        }
        eventRef.addValueEventListener(eventListener)
        awaitClose { eventRef.removeEventListener(eventListener) }
    }

}