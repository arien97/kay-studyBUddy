@file:OptIn(ExperimentalMaterialApi::class)

package com.example.studybuddy.features.course

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController

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

    val selected by viewModel.selected.collectAsState()
    val courses by viewModel.courses.collectAsState()
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {

        Text(
            text = "Course search",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally),
        )

        LazyColumn(
            Modifier
                .fillMaxWidth(0.5f)
                .weight(1f)
                .align(androidx.compose.ui.Alignment.CenterHorizontally)
        ) {
            items(courses) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onCourseClick(it) }
                    .padding(16.dp)
                ) {
                    Text(
                        text = it,
                        modifier = Modifier
                            .weight(1f)
                            .align(CenterVertically)
                    )

                    if (selected.contains(it)) {
                        Icon(Icons.Default.Done, contentDescription = null)
                    }

                }
            }
        }

        if (selected.isNotEmpty()) {
            Button(
                content = { androidx.compose.material3.Text("Confirm") },
                onClick = { viewModel.confirm() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(10.dp)
            )
        }


    }
}
