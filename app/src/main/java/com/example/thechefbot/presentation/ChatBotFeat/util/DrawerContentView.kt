package com.example.thechefbot.presentation.ChatBotFeat.util

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.thechefbot.R
import com.example.thechefbot.presentation.ChatBotFeat.model.data.ChatSession
import kotlin.collections.forEach

@Composable
fun DrawerContentView(
    modifier: Modifier,
    context: android.content.Context,
    expandDrawer: () -> Unit,
    showDeleteDialogStatus: Boolean = false,
    newChat:() -> Unit,
    showDeleteDialog: () -> Unit,
    allSessions: List<ChatSession>,
    sessionToDelete: (Int?) -> Unit,
    onItemClicked : (Int) -> Unit,
    activeSessionId: Int?
) {
    Box(modifier = modifier
        .padding(horizontal = 16.dp)) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))

            IconButton(onClick = {
                expandDrawer()
            }) {
                Icon(
                    Icons.Default.MenuOpen, contentDescription = "Menu",
                    tint = colorResource(R.color.orange)
                )
            }

            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "user attachment",
                contentScale = ContentScale.FillWidth,
                modifier = modifier
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                "Funmito",
                modifier = modifier
                    .padding(5.dp)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.titleMedium
            )
            HorizontalDivider()

            NavigationDrawerItem(
                modifier = modifier
                    .padding(10.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(16.dp),
                label = { Text("New Chat") },
                selected = false,
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_chat_save),
                        contentDescription = null
                    )
                },
                onClick = {
                    newChat()
                }
            )
            NavigationDrawerItem(
                modifier = modifier
                    .padding(10.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(16.dp),
                label = { Text("Delete All Chats") },
                selected = false,
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_chat_del),
                        contentDescription = null
                    )
                },
                onClick = {
                    showDeleteDialog()
                }
            )

            HorizontalDivider(modifier = modifier.padding(vertical = 8.dp))

            Text(
                "Chat History",
                modifier = modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.titleMedium,
                color = colorResource(R.color.orange)
            )

            // LIST OF ALL SESSIONS
            allSessions.forEach { session ->
                ChatHistoryItem(
                    modifier = modifier,
                    session = session,
                    allSessions = allSessions,
                    sessionToDelete = { sessionToDelete(it) },
                    activeSessionId = activeSessionId,
                    onItemClicked = {
                        onItemClicked(it)
                    }
                )
            }
            Spacer(modifier.height(12.dp))
        }

        if (showDeleteDialogStatus){
            Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show()
        }
    }
}





@Composable
fun ChatHistoryItem(
    modifier: Modifier,
    session: ChatSession,
    allSessions: List<ChatSession>,
    sessionToDelete: (Int?) -> Unit,
    onItemClicked : (Int) -> Unit,
    activeSessionId: Int?,
) {
    NavigationDrawerItem(
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 10.dp),
        shape = RoundedCornerShape(12.dp),
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_chat_options),
                contentDescription = null
            )
        },
        label = {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (session.title.isNullOrEmpty()) "New Chat" else session.title,
                    modifier = modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (allSessions.size > 1) {
                    IconButton(
                        onClick = {
                            sessionToDelete(session.sessionId)
                        }
                    ) {
                        Icon(
                            Icons.Default.Cancel,
                            contentDescription = "Delete session",
                            tint = colorResource(R.color.orange)
                        )
                    }
                }
            }
        },
        selected = activeSessionId == session.sessionId,
        onClick = {
            onItemClicked(session.sessionId)
        }
    )
}