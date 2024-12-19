@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.studybuddy.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.studybuddy.R

@Composable
fun AppToolbar(title: String, backAction: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            Image(
                painterResource(R.drawable.ic_arrow_back),
                contentDescription = null, Modifier.clickable { backAction.invoke() }
            )
        }
    )
}