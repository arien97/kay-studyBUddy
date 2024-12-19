package com.example.studybuddy.features.chat

// Compose UI imports
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState

// Material Design imports
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School

// Compose Runtime imports
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// Compose UI Platform imports
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// Hilt imports
import androidx.hilt.navigation.compose.hiltViewModel

// Your app's imports
import com.example.studybuddy.MessageRegister
import com.example.studybuddy.NavigationRoute
import com.example.studybuddy.domain.MessageStatus
import com.example.studybuddy.domain.User
import com.example.studybuddy.features.chat.chatInput.ChatInput
import com.example.studybuddy.features.chat.chatrow.ReceivedMessageRow
import com.example.studybuddy.features.chat.chatrow.SentMessageRow

// Java imports
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ChatScreen(
    chatInfo: NavigationRoute.Chat,
    chatViewModel: ChatScreenViewModel = hiltViewModel(),
) {

    when (chatInfo) {
        is NavigationRoute.Chat.CourseChat -> {
            chatViewModel.loadCourseMessagesFromFirebase(chatInfo.course)
        }

        is NavigationRoute.Chat.PrivateChat -> {
            chatViewModel.loadMessagesFromFirebase(
                chatInfo.chatRoomUUID,
                chatInfo.opponentUUID,
                chatInfo.registerUUID
            )
            LaunchedEffect(key1 = Unit) {
                chatViewModel.loadOpponentProfileFromFirebase(chatInfo.opponentUUID)
            }
        }
    }
    // State declarations
    val messages = chatViewModel.messages
    var opponentProfileFromFirebase by remember { mutableStateOf(User()) }
    opponentProfileFromFirebase = chatViewModel.opponentProfileFromFirebase.value
    val opponentName = opponentProfileFromFirebase.userName
    val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = messages.size)
    val messagesLoadedFirstTime = chatViewModel.messagesLoadedFirstTime.value
    val messageInserted = chatViewModel.messageInserted.value
    var isChatInputFocus by remember { mutableStateOf(false) }

    // Auto-scroll effect
    LaunchedEffect(key1 = messagesLoadedFirstTime, messages, messageInserted) {
        if (messages.isNotEmpty()) {
            scrollState.scrollToItem(index = messages.size - 1)
        }
    }

    // IME padding handling
    val imePaddingValues = PaddingValues()
    val imeBottomPadding = imePaddingValues.calculateBottomPadding().value.toInt()
    LaunchedEffect(key1 = imeBottomPadding) {
        if (messages.isNotEmpty()) {
            scrollState.scrollToItem(index = messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .focusable()
            .wrapContentHeight()
            .safeDrawingPadding()
            .imePadding()
    ) {
        // Top Bar with chat info
        TopAppBar(
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (chatInfo) {
                    is NavigationRoute.Chat.CourseChat -> {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Course",
                            tint = MaterialTheme.colors.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Course: ${chatInfo.course}",
                            style = MaterialTheme.typography.h6
                        )
                    }
                    is NavigationRoute.Chat.PrivateChat -> {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Private Chat",
                            tint = MaterialTheme.colors.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = opponentName,
                            style = MaterialTheme.typography.h6
                        )
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = scrollState
        ) {
            items(messages) { message: MessageRegister ->
                val sdf = remember {
                    java.text.SimpleDateFormat("hh:mm", Locale.ROOT)
                }

                when (message.isMessageFromOpponent) {
                    true -> {
                        ReceivedMessageRow(
                            text = message.chatMessage.message,
                            opponentName = opponentName,
                            quotedMessage = null,
                            messageTime = sdf.format(message.chatMessage.date),
                        )
                    }
                    false -> {
                        SentMessageRow(
                            text = message.chatMessage.message,
                            quotedMessage = null,
                            messageTime = sdf.format(message.chatMessage.date),
                            messageStatus = MessageStatus.valueOf(message.chatMessage.status)
                        )
                    }
                }
            }
        }

        // Chat Input
        ChatInput(
            onMessageChange = { messageContent ->
                when (chatInfo) {
                    is NavigationRoute.Chat.CourseChat -> {
                        chatViewModel.insertCourseMessageToFirebase(
                            chatInfo.course, messageContent
                        )
                    }
                    is NavigationRoute.Chat.PrivateChat -> {
                        chatViewModel.insertMessageToFirebase(
                            chatInfo.chatRoomUUID,
                            messageContent,
                            chatInfo.registerUUID,
                        )
                    }
                }
            },
            onFocusEvent = {
                isChatInputFocus = it
            }
        )
    }
}
