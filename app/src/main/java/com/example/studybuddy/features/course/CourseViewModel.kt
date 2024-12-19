package com.example.studybuddy.features.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studybuddy.data.CoursesRepository
import com.example.studybuddy.data.ProfileRepository
import com.example.studybuddy.utils.ToastNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    coursesRepository: CoursesRepository,
    private val toastNotifier: ToastNotifier,
) : ViewModel() {

    private val _complete = Channel<Unit>(Channel.CONFLATED)
    val complete get() = _complete.receiveAsFlow()

    private val _selected = MutableStateFlow<Set<String>>(emptySet())
    val selected = _selected.asStateFlow()

    val courses = coursesRepository.observeCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            _selected.value = profileRepository.getCourses().fold(
                { it }, { toastNotifier.showMessage(it.message);emptySet() }
            )
        }
    }

    fun onCourseClick(course: String) {
        _selected.update {
            val mutable = it.toMutableSet()
            if (mutable.remove(course)) {
                mutable
            } else {
                mutable + course
            }.toSet()
        }
    }

    fun confirm() {
        viewModelScope.launch {
            profileRepository.updateCourse(selected.value)
            _complete.send(Unit)
        }
    }

}
