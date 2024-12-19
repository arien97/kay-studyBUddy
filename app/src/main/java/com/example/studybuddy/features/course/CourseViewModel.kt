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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// CourseViewModel.kt
@HiltViewModel
class CourseViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    coursesRepository: CoursesRepository,
    private val toastNotifier: ToastNotifier,
) : ViewModel() {

    private val _complete = Channel<Unit>(Channel.CONFLATED)
    val complete get() = _complete.receiveAsFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedAcademicUnit = MutableStateFlow<String?>(null)
    val selectedAcademicUnit = _selectedAcademicUnit.asStateFlow()

    private val _selectedDepartment = MutableStateFlow<String?>(null)
    val selectedDepartment = _selectedDepartment.asStateFlow()

    private val _selected = MutableStateFlow<Set<String>>(emptySet())
    val selected = _selected.asStateFlow()

    val academicUnits = coursesRepository.observeAcademicUnits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val departments = _selectedAcademicUnit
        .flatMapLatest { coursesRepository.observeDepartments(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredCourses = combine(
        coursesRepository.observeCourses(),
        _searchQuery,
        _selectedAcademicUnit,
        _selectedDepartment
    ) { courses, query, unit, dept ->
        courses.filter { course ->
            val matchesSearch = query.isEmpty() ||
                    course.courseName.contains(query, ignoreCase = true) ||
                    course.fullCourseCode.contains(query, ignoreCase = true)
            val matchesUnit = unit == null || course.academicUnit == unit
            val matchesDept = dept == null || course.department == dept
            matchesSearch && matchesUnit && matchesDept
        }.sortedBy { it.courseName }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            _selected.value = profileRepository.getCourses().fold(
                { it }, { toastNotifier.showMessage(it.message); emptySet() }
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectAcademicUnit(unit: String?) {
        _selectedAcademicUnit.value = unit
        _selectedDepartment.value = null
    }

    fun selectDepartment(dept: String?) {
        _selectedDepartment.value = dept
    }

    fun onCourseClick(courseCode: String) {
        _selected.update {
            val mutable = it.toMutableSet()
            if (mutable.remove(courseCode)) {
                mutable
            } else {
                mutable + courseCode
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
