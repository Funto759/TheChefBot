package com.example.thechefbot.presentation.ChatBotFeat.util

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thechefbot.R
import com.example.thechefbot.presentation.ChatBotFeat.data.ChatMessage
import com.example.thechefbot.presentation.ChatBotFeat.data.ChatSession
import com.example.thechefbot.presentation.ChatBotFeat.state.ChefUiState
import com.example.thechefbot.util.CommonUtil.formatTimestamp


@Composable
fun InitialConversationScreen(
    modifier: Modifier,
    paddingValues: PaddingValues,
    session: ChatSession?,
    messages: List<ChatMessage>
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            EmptyConversationScreen(modifier = modifier, session = session, messages = messages)
        }
    }
}


@Composable
fun EmptyConversationScreen(
    modifier: Modifier = Modifier,
    session: ChatSession?,
    messages: List<ChatMessage>
) {
    Icon(
        painter = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = "",
        tint = Color.Unspecified,
        modifier = modifier.padding(5.dp)
    )
    Text(text = "Hello, Ask me Anything....", fontSize = 24.sp)
    session?.lastUsedTimeStamp?.let {
        if (messages.isEmpty()){
            Text(text = "Created At: ${formatTimestamp(session.lastUsedTimeStamp)}")
        }else{
            Text(text = "Last Update: ${formatTimestamp(session.lastUsedTimeStamp)}")
        }
    }
    ContentBody(modifier = Modifier)
}

@Composable
fun ContentBody(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(24.dp))

    Icon(
        painter = painterResource(R.drawable.ic_ai_o),
        contentDescription = "",
        tint = Color.Unspecified
    )
    Spacer(modifier = modifier.height(14.dp))
    ElevatedCardExample()
    Spacer(modifier = modifier.height(14.dp))
    ElevatedCardExample()
    Spacer(modifier = modifier.height(24.dp))

    Icon(
        painter = painterResource(R.drawable.ic_ai_p),
        contentDescription = "",
        tint = Color.Unspecified,
    )
    Spacer(modifier = modifier.height(14.dp))
    ElevatedCardExample(image = painterResource(R.drawable.ic_marker_2))
    Spacer(modifier = modifier.height(14.dp))
    ElevatedCardExample(image = painterResource(R.drawable.ic_marker_2))
    Spacer(modifier = modifier.height(24.dp))
}



@Composable
fun MessagesList(
    modifier: Modifier,
    session: ChatSession?,
    paddingValues: PaddingValues,
    messages: List<ChatMessage>,
    context: Context,
    chefUiState: ChefUiState,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(paddingValues = paddingValues),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
       items(items = messages, key = { it.messageId }){ msg ->
            ChatMessageRow(
                msg = msg
                , showAlertDialog = chefUiState.showDeleteDialog
                , context = context
                ,session = session
                , onDismiss = {
                    onDismiss()
                }
                , onConfirm = {
                    onConfirm()
                }
                , onCancel = {
                    onCancel()
                }
            )
        }

        if (chefUiState.loading) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    ChatBubble(
                        text = chefUiState.prompt,
                        timestamp = 11,
                        isUser = true,
                        isMarkdown = false,
                        loading = true
                    )
                }
            }
        }

        if (chefUiState.errorState) {
            item {
                Text(
                    text = chefUiState.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
