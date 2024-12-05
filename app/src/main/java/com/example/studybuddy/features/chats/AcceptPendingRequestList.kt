package com.example.studybuddy.features.chats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.studybuddy.domain.FriendListRow
import com.example.studybuddy.domain.MessageStatus
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AcceptPendingRequestList(
    item: FriendListRow,
    onclick: () -> Unit = {}
) {
    Row (
        modifier = Modifier
            .fillMaxSize()
            .clickable { onclick() }
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(60.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .aspectRatio(1f)
                )
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            val sdf = remember { SimpleDateFormat("hh:mm", Locale.ROOT) }
            if (item.lastMessage.status == MessageStatus.RECEIVED.toString() && item.lastMessage.profileUUID == item.userUUID) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = item.userEmail,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Last Message: " + item.lastMessage.message,
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                    Column {
                        Text(
                            text = sdf.format(
                                item.lastMessage.date
                            ),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )

                        Icon(
                            imageVector = Icons.Filled.MarkEmailUnread,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                }
            } else {
                val dateTimeControl: Long = 0
                if (!item.lastMessage.date.equals(dateTimeControl)) {

                    if (item.lastMessage.profileUUID != item.userUUID) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = item.userEmail,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Me: " + item.lastMessage.message,
                                    style = MaterialTheme.typography.titleSmall,

                                    )
                            }
                            Text(
                                text = sdf.format(
                                    item.lastMessage.date
                                ),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = item.userEmail,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Last Message: " + item.lastMessage.message,
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }
                            Text(
                                text = sdf.format(
                                    item.lastMessage.date
                                ),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }

                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(start = 10.dp)
                    ) {
                        Text(
                            text = item.userEmail,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(2.dp)
                        )
                    }
                }
            }
        }
    }

}