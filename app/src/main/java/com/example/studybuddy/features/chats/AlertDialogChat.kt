package com.example.studybuddy.features.chats

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.studybuddy.utils.AlertDialogCustomOutlinedTextField

@Composable
fun AlertDialogChat(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val dialogText = "Add user via email".trimIndent()
    var emailInput by remember {
        mutableStateOf("")
    }
    AlertDialog(
        icon = {
               Icon(imageVector = Icons.Filled.Person, contentDescription = null)
        },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Add User",
                textAlign = TextAlign.Center
            )
        },

        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(emailInput)
                }
            ) {
                Text(text = "OK")
            }
        },

        text = {
            AlertDialogCustomOutlinedTextField(
                entry = emailInput,
                hint = "email",
                onChange = { emailInput = it })
        }
    )
}