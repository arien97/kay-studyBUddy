package com.example.studybuddy.features.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.example.studybuddy.R

@Composable
fun SignInRoute(
    signUp: () -> Unit,
    authFlow: () -> Unit,
    emailLink: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(Unit) {
        viewModel.authComplete.flowWithLifecycle(lifecycle = lifecycle)
            .collect { authFlow.invoke() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Logo and App Name
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "BU Study Buddy Logo",
            contentScale = androidx.compose.ui.layout.ContentScale.Fit,
            // Removed the ColorFilter since we want to show the logo in its original colors
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 24.dp)
        )

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var emailError by remember { mutableStateOf<String?>(null) }

        // Email Field with BU validation
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = if (!it.endsWith("@bu.edu") && it.isNotEmpty()) {
                    "Please use your BU email (@bu.edu)"
                } else null
            },
            label = { Text("BU Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailError != null,
            supportingText = {
                emailError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        val isLoading by viewModel.isLoading.collectAsState()
        if (!isLoading) {
            Button(
                onClick = {
                    if (email.endsWith("@bu.edu")) {
                        viewModel.signIn(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                enabled = email.endsWith("@bu.edu") && password.isNotEmpty()
            ) {
                Text(
                    text = "Sign in",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Up Link
        TextButton(
            onClick = { signUp.invoke() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "New to Study BUddy? Sign up",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

//        // Email Verification Link
//        TextButton(
//            onClick = { emailLink.invoke() },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(
//                text = "Sign in with email verification",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.primary
//            )
//        }
    }
}