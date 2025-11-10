package com.example.thechefbot.presentation.ChatBotFeat.util

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thechefbot.R
import com.example.thechefbot.presentation.ChatBotFeat.data.ChatMessage
import com.example.thechefbot.presentation.ChatBotFeat.data.ChatSession
import com.example.thechefbot.util.CommonUtil.formatTimestamp
import kotlinx.coroutines.launch


@Composable
fun InitialConversationScreen(
    modifier: Modifier,
    paddingValues: PaddingValues,
    session: ChatSession?,
    messages: List<ChatMessage>,
    showDeleteDialog: Boolean = false,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
    onDismissRename: () -> Unit = {},
    onConfirmRename: () -> Unit = {},
    onCancelRename: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
    renameText : String,
    showRenameDialog: Boolean = false,
) {
    Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(paddingValues = paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            EmptyConversationScreen(modifier = modifier, session = session, messages = messages)
        }
    }
        if (showRenameDialog) {
            ChangeTitleDialog(
                modifier = modifier,
                value = renameText,
                onValueChange = onValueChange,
                onDismissRequest = onDismissRename,
                onConfirm = onConfirmRename,
                onCancel = onCancelRename
            )
        }

        if (showDeleteDialog) {
            ConfirmDeleteDialog(
                sessionToDelete = session?.sessionId,
                onDismissRequest = onDismiss,
                onConfirm = onConfirm,
                onCancel = onCancel
            )
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
    listState: LazyListState,
    showDeleteDialog: Boolean = false,
    showRenameDialog: Boolean = false,
    loading: Boolean = false,
    prompt: String = "",
    errorState: Boolean = false,
    error: String = "",
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {} ,
    onDismissRename: () -> Unit = {},
    onConfirmRename: () -> Unit = {},
    onCancelRename: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
    renameText : String,
) {


    val scope = rememberCoroutineScope()

    // Show button when last visible index isn’t the final list item
    val showScrollToBottom by remember {
        derivedStateOf { listState.canScrollForward }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(paddingValues = paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            contentPadding = PaddingValues(bottom = 64.dp)
        ) {

            items(items = messages, key = { it.messageId }) { msg ->
                ChatMessageRow(
                    msg = msg,
                    showAlertDialog = showDeleteDialog,
                    context = context,
                    session = session,
                    onDismiss = onDismiss,
                    onConfirm = onConfirm,
                    onCancel = onCancel
                )
            }

            item {
                AnimatedVisibility(
                    visible = loading
                ) {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        ChatBubble(
                            text = prompt,
                            timestamp = 11,
                            isUser = true,
                            modifier = modifier,
                            isMarkdown = false,
                            loading = true
                        )
                    }
                }
            }


//            if (loading) {
//                item {
//                    Row(
//                        modifier = modifier
//                            .fillMaxWidth()
//                            .padding(16.dp),
//                        horizontalArrangement = Arrangement.End
//                    ) {
//                        ChatBubble(
//                            text = prompt,
//                            timestamp = 11,
//                            isUser = true,
//                            modifier = modifier,
//                            isMarkdown = false,
//                            loading = true
//                        )
//                    }
//                }
//            }

            if (errorState) {
                item {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // Scroll-to-bottom button
        AnimatedVisibility(
            visible = showScrollToBottom,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(end = 16.dp, bottom = 126.dp)
        ) {
            SmallFloatingActionButton(
                containerColor = colorResource(R.color.orange),
                onClick = {
                    scope.launch {
                        // Scroll to very last composed item (accounts for loading/error items)
                        val lastIndex = maxOf(0, listState.layoutInfo.totalItemsCount - 1)
                        listState.animateScrollToItem(lastIndex)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Scroll to bottom",
                    tint = Color.DarkGray
                )
            }
        }

        if (showRenameDialog) {
            ChangeTitleDialog(
                modifier = modifier,
                value = renameText,
                onValueChange = onValueChange,
                onDismissRequest = onDismissRename,
                onConfirm = onConfirmRename,
                onCancel = onCancelRename
            )
        }

        if (showDeleteDialog) {
            ConfirmDeleteDialog(
                sessionToDelete = session?.sessionId,
                onDismissRequest = onDismiss,
                onConfirm = onConfirm,
                onCancel = onCancel
            )
        }

    }
}
