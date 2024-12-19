package com.example.studybuddy.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studybuddy.domain.Event

@Composable
fun EventPreviewCard(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showArrow: Boolean = true
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.title.orEmpty(),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = event.course.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = event.date.orEmpty(),
                    style = MaterialTheme.typography.bodySmall
                )
                if (event.startTime != null && event.endTime != null) {
                    Text(
                        text = "${event.startTime} - ${event.endTime}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = "Posted by: ${event.authorUsername}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            if (showArrow) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Go to Event Details",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}