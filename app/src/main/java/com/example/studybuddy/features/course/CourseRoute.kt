@file:OptIn(ExperimentalMaterialApi::class)

package com.example.studybuddy.features.course

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CourseRoute(
    navController: NavController,
    viewModel: CourseViewModel = hiltViewModel()
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(Unit) {
        viewModel.complete.flowWithLifecycle(lifecycle = lifecycle)
            .collect { navController.navigateUp() }
    }

    var isSchoolExpanded by remember { mutableStateOf(false) }
    var isDepartmentExpanded by remember { mutableStateOf(false) }

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedAcademicUnit by viewModel.selectedAcademicUnit.collectAsState()
    val selectedDepartment by viewModel.selectedDepartment.collectAsState()
    val selected by viewModel.selected.collectAsState()
    val courses by viewModel.filteredCourses.collectAsState()
    val academicUnits by viewModel.academicUnits.collectAsState()
    val departments by viewModel.departments.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Course search",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.align(CenterHorizontally)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::updateSearchQuery,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            placeholder = { Text("Search courses by name or code...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // School Dropdown
            ExposedDropdownMenuBox(
                modifier = Modifier.weight(1f),
                expanded = isSchoolExpanded,
                onExpandedChange = { isSchoolExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedAcademicUnit ?: "All Schools",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = isSchoolExpanded,
                    onDismissRequest = { isSchoolExpanded = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            viewModel.selectAcademicUnit(null)
                            isSchoolExpanded = false
                        }
                    ) {
                        Text("All Schools")
                    }
                    academicUnits.forEach { unit ->
                        DropdownMenuItem(
                            onClick = {
                                viewModel.selectAcademicUnit(unit)
                                isSchoolExpanded = false
                            }
                        ) {
                            Text(unit)
                        }
                    }
                }
            }

            // Department Dropdown
            ExposedDropdownMenuBox(
                modifier = Modifier.weight(1f),
                expanded = isDepartmentExpanded,
                onExpandedChange = { isDepartmentExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedDepartment ?: "All Departments",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = isDepartmentExpanded,
                    onDismissRequest = { isDepartmentExpanded = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            viewModel.selectDepartment(null)
                            isDepartmentExpanded = false
                        }
                    ) {
                        Text("All Departments")
                    }
                    departments.forEach { dept ->
                        DropdownMenuItem(
                            onClick = {
                                viewModel.selectDepartment(dept)
                                isDepartmentExpanded = false
                            }
                        ) {
                            Text(dept)
                        }
                    }
                }
            }
        }

        LazyColumn(
            Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(courses) { course ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onCourseClick(course.fullCourseCode) }
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .align(CenterVertically)
                    ) {
                        Text(
                            text = course.courseName,
                            style = MaterialTheme.typography.subtitle1
                        )
                        Text(
                            text = course.fullCourseCode,
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    if (selected.contains(course.fullCourseCode)) {
                        Icon(
                            Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.align(CenterVertically)
                        )
                    }
                }
            }
        }

        if (selected.isNotEmpty()) {
            Button(
                onClick = { viewModel.confirm() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Confirm")
            }
        }
    }
}