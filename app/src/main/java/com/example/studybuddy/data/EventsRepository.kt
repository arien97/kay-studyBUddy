package com.example.studybuddy.data

import com.example.studybuddy.domain.Event
import com.example.studybuddy.domain.EventCreate
import com.example.studybuddy.utils.suspendRunCatching
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
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
    private val userCalendarReference = database.getReference("userCalendars")

    /**
     * Creates or updates an event in the database
     * @param eventCreate The event details to be created/updated
     * @return Result indicating success or failure
     */
    suspend fun createOrUpdateEvent(eventCreate: EventCreate): Result<Unit> = suspendRunCatching {
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
        childUpdates["createdAt"] = ServerValue.TIMESTAMP

        eventsDatabaseReference
            .child(UUID.randomUUID().toString())
            .updateChildren(childUpdates)
            .await()
    }

    /**
     * Observes all events in the system (for discovery page)
     * @return Flow of all events
     */
    fun observeAllEvents(): Flow<List<Event>> = callbackFlow {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = snapshot.children.mapNotNull {
                    val event = it.getValue(Event::class.java) ?: return@mapNotNull null
                    val id = it.key ?: return@mapNotNull null
                    event.copy(id = id)
                }
                this@callbackFlow.trySendBlocking(events)
            }

            override fun onCancelled(error: DatabaseError) = Unit
        }
        eventsDatabaseReference.addValueEventListener(eventListener)
        awaitClose { eventsDatabaseReference.removeEventListener(eventListener) }
    }

    /**
     * Observes only the current user's personal events
     * @return Flow of user's personal events
     */
    fun observePersonalEvents(): Flow<List<Event>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = snapshot.children.mapNotNull {
                    val event = it.getValue(Event::class.java) ?: return@mapNotNull null
                    val id = it.key ?: return@mapNotNull null
                    if (event.authorUUID == currentUserId) {
                        event.copy(id = id)
                    } else {
                        null
                    }
                }
                this@callbackFlow.trySendBlocking(events)
            }

            override fun onCancelled(error: DatabaseError) = Unit
        }
        eventsDatabaseReference.addValueEventListener(eventListener)
        awaitClose { eventsDatabaseReference.removeEventListener(eventListener) }
    }

    /**
     * Observes a specific event by ID
     * @param id The event ID
     * @return Flow of the specific event
     */
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

    /**
     * Checks if an event is in the user's calendar
     * @param eventId The event ID to check
     * @return Boolean indicating if event is in calendar
     */
    suspend fun isEventInCalendar(eventId: String): Boolean = suspendRunCatching {
        val userId = auth.currentUser?.uid ?: return@suspendRunCatching false
        val snapshot = userCalendarReference
            .child(userId)
            .child(eventId)
            .get()
            .await()
        return@suspendRunCatching snapshot.exists()
    }.getOrDefault(false)

    /**
     * Adds an event to the user's calendar
     * @param eventId The event ID to add
     * @return Result indicating success or failure
     */
    suspend fun addEventToCalendar(eventId: String): Result<Unit> = suspendRunCatching {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val calendarEntry = mapOf(
            "addedAt" to ServerValue.TIMESTAMP,
            "eventId" to eventId
        )
        userCalendarReference
            .child(userId)
            .child(eventId)
            .setValue(calendarEntry)
            .await()
    }

    /**
     * Removes an event from the user's calendar
     * @param eventId The event ID to remove
     * @return Result indicating success or failure
     */
    suspend fun removeEventFromCalendar(eventId: String): Result<Unit> = suspendRunCatching {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        userCalendarReference
            .child(userId)
            .child(eventId)
            .removeValue()
            .await()
    }

    /**
     * Deletes an event from the system
     * @param eventId The event ID to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteEvent(eventId: String): Result<Unit> = suspendRunCatching {
        val currentUserId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

        // First verify this is the user's event
        val event = eventsDatabaseReference.child(eventId).get().await()
            .getValue(Event::class.java) ?: throw IllegalStateException("Event not found")

        if (event.authorUUID != currentUserId) {
            throw IllegalStateException("Not authorized to delete this event")
        }

        // Delete the event
        eventsDatabaseReference
            .child(eventId)
            .removeValue()
            .await()

        // Also remove it from all user calendars who added it
        userCalendarReference
            .get()
            .await()
            .children
            .forEach { userSnapshot ->
                userSnapshot.child(eventId).ref.removeValue().await()
            }
    }

    /**
     * Updates an existing event
     * @param eventId The event ID to update
     * @param eventUpdate The updated event details
     * @return Result indicating success or failure
     */
    suspend fun updateEvent(eventId: String, eventUpdate: EventCreate): Result<Unit> = suspendRunCatching {
        val currentUserId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

        // First verify this is the user's event
        val event = eventsDatabaseReference.child(eventId).get().await()
            .getValue(Event::class.java) ?: throw IllegalStateException("Event not found")

        if (event.authorUUID != currentUserId) {
            throw IllegalStateException("Not authorized to update this event")
        }

        val updates = mutableMapOf<String, Any?>()
        updates["title"] = eventUpdate.title
        updates["description"] = eventUpdate.description
        updates["course"] = eventUpdate.course
        updates["date"] = eventUpdate.date
        updates["startTime"] = eventUpdate.startTime
        updates["endTime"] = eventUpdate.endTime
        updates["latitude"] = eventUpdate.latitude
        updates["longitude"] = eventUpdate.longitude
        updates["location"] = eventUpdate.location
        updates["updatedAt"] = ServerValue.TIMESTAMP

        eventsDatabaseReference
            .child(eventId)
            .updateChildren(updates)
            .await()
    }

    /**
     * Gets events for a specific course
     * @param courseName The name of the course
     * @return Flow of events for that course
     */
    fun observeCourseEvents(courseName: String): Flow<List<Event>> = callbackFlow {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = snapshot.children.mapNotNull {
                    val event = it.getValue(Event::class.java) ?: return@mapNotNull null
                    val id = it.key ?: return@mapNotNull null
                    if (event.course == courseName) {
                        event.copy(id = id)
                    } else {
                        null
                    }
                }
                this@callbackFlow.trySendBlocking(events)
            }

            override fun onCancelled(error: DatabaseError) = Unit
        }
        eventsDatabaseReference.addValueEventListener(eventListener)
        awaitClose { eventsDatabaseReference.removeEventListener(eventListener) }
    }

    // Function to observe both personal events and calendar events
    fun observeCalendarAndPersonalEvents(): Flow<List<Event>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?: return@callbackFlow

        // Reference to user's calendar
        val userCalendarRef = userCalendarReference.child(currentUserId)
        val eventsRef = eventsDatabaseReference

        // Create listener for calendar changes
        val calendarListener = object : ValueEventListener {
            override fun onDataChange(calendarSnapshot: DataSnapshot) {
                // Get set of event IDs in user's calendar
                val calendarEventIds = calendarSnapshot.children.mapNotNull { it.key }.toSet()

                // Add listener for events
                eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(eventsSnapshot: DataSnapshot) {
                        val allEvents = eventsSnapshot.children.mapNotNull {
                            val event = it.getValue(Event::class.java) ?: return@mapNotNull null
                            val id = it.key ?: return@mapNotNull null
                            event.copy(id = id)
                        }

                        // Filter events that are either created by user or in their calendar
                        val relevantEvents = allEvents.filter { event ->
                            event.authorUUID == currentUserId || event.id in calendarEventIds
                        }

                        trySendBlocking(relevantEvents)
                    }

                    override fun onCancelled(error: DatabaseError) = Unit
                })
            }

            override fun onCancelled(error: DatabaseError) = Unit
        }

        // Start listening to calendar changes
        userCalendarRef.addValueEventListener(calendarListener)

        // Cleanup listener when flow is cancelled
        awaitClose {
            userCalendarRef.removeEventListener(calendarListener)
        }
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

}