@file:OptIn(ExperimentalLayoutApi::class)

package com.example.studybuddy.features.signbylink

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.studybuddy.R
import com.example.studybuddy.utils.AppToolbar

@Composable
fun SignByLinkRoute(
    backAction: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignByLinkViewModel = hiltViewModel()
) {

    Column(
        modifier
            .fillMaxSize()
            .safeDrawingPadding(),
    ) {
        AppToolbar(title = "Sign up", backAction = { backAction.invoke() })
        Image(
            painterResource(R.drawable.ic_landscape),
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth(0.35f)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = "BUddy",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )
        var email: String by remember { mutableStateOf("") }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        val isLoading by viewModel.isLoading.collectAsState()
        if (!isLoading) {
            Button(
                content = { Text("Send email") },
                onClick = { viewModel.sentEmail(email) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(10.dp)
            )
        } else {
            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
        }
    }
}