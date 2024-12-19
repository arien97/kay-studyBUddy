package com.example.studybuddy.data

import com.example.studybuddy.domain.Course
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoursesRepository @Inject constructor() {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val coursesDatabaseReference = database.getReference("courses")

    fun observeCourses(): Flow<List<Course>> = callbackFlow {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courses = snapshot.children.mapNotNull { courseSnapshot ->
                    val courseMap = courseSnapshot.value as? Map<*, *>
                    if (courseMap != null) {
                        Course.fromMap(courseMap.mapKeys { it.key.toString() })
                    } else null
                }
                this@callbackFlow.trySendBlocking(courses)
            }

            override fun onCancelled(error: DatabaseError) = Unit
        }
        coursesDatabaseReference.addValueEventListener(eventListener)
        awaitClose { coursesDatabaseReference.removeEventListener(eventListener) }
    }

    fun observeAcademicUnits(): Flow<List<String>> = callbackFlow {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val units = snapshot.children.mapNotNull { courseSnapshot ->
                    val courseMap = courseSnapshot.value as? Map<*, *>
                    courseMap?.get("academicUnit") as? String
                }.distinct().sorted()
                this@callbackFlow.trySendBlocking(units)
            }

            override fun onCancelled(error: DatabaseError) = Unit
        }
        coursesDatabaseReference.addValueEventListener(eventListener)
        awaitClose { coursesDatabaseReference.removeEventListener(eventListener) }
    }

    fun observeDepartments(academicUnit: String? = null): Flow<List<String>> = callbackFlow {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val departments = snapshot.children.mapNotNull { courseSnapshot ->
                    val courseMap = courseSnapshot.value as? Map<*, *>
                    val dept = courseMap?.get("department") as? String
                    val unit = courseMap?.get("academicUnit") as? String
                    if (academicUnit == null || unit == academicUnit) dept else null
                }.distinct().sorted()
                this@callbackFlow.trySendBlocking(departments)
            }

            override fun onCancelled(error: DatabaseError) = Unit
        }
        coursesDatabaseReference.addValueEventListener(eventListener)
        awaitClose { coursesDatabaseReference.removeEventListener(eventListener) }
    }

    suspend fun addCourse(course: Course) {
        coursesDatabaseReference.child(course.fullCourseCode).setValue(course.toMap())
    }

    suspend fun removeCourse(courseCode: String) {
        coursesDatabaseReference.child(courseCode).removeValue()
    }
}