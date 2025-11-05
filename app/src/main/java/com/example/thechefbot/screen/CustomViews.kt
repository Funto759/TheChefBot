package com.example.thechefbot.screen

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.thechefbot.R
import com.example.thechefbot.model.data.ChatMessage
import com.example.thechefbot.model.data.ChatSession
import com.example.thechefbot.model.state.ChefUiState
import com.example.thechefbot.util.CommonUtil.copyToClipboard
import com.example.thechefbot.util.CommonUtil.parseMarkdown
import com.example.thechefbot.util.shimmer


@Composable
fun MessagesList(
    modifier: Modifier,
    paddingValues: PaddingValues,
    messages: List<ChatMessage>,
    context: Context,
    chefUiState: ChefUiState
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(paddingValues = paddingValues),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        items(messages.size) { index ->
            val msg = messages[index]
            ChatMessageRow(msg = msg, context = context)
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
fun SendButton(modifier: Modifier
               ,onSendClicked: () -> Unit
) {
    IconButton(
        modifier = modifier.padding(5.dp),
        onClick = {
            onSendClicked()
        }) {
        Icon(Icons.Filled.Send, contentDescription = "Send message",
            tint  = colorResource(R.color.orange))
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopChefBar(modifier: Modifier = Modifier, scrollBehavior: TopAppBarScrollBehavior, onClick: () -> Unit, text: String?) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = if (text.isNullOrEmpty()) "Recipe Generator" else text)
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "",
                    tint = colorResource(R.color.orange)
                )
            }

        },
        navigationIcon = {
            IconButton(onClick = {onClick()}) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "",
                    tint = colorResource(R.color.orange)
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
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


//    Icon(
//        painter = painterResource(R.drawable.ic_sun),
//        contentDescription = "",
//        tint = Color.Unspecified
//    )
//    Spacer(modifier = modifier.height(14.dp))
//    ElevatedCardExample(image = painterResource(R.drawable.ic_marker_2))
//    Spacer(modifier = modifier.height(14.dp))
//    ElevatedCardExample(image = painterResource(R.drawable.ic_marker_2))
//    Spacer(modifier = modifier.height(24.dp))

}




@Composable
fun ChatBubble(
    text: String,
    timestamp: Long,
    isUser: Boolean,
    isMarkdown: Boolean,
    modifier: Modifier = Modifier,
    loading : Boolean = false,
    onClick : () -> Unit = {}
) {
    // colors
    val bubbleColor =
        if (isUser) Color.DarkGray
        else Color.DarkGray

    val textColor =
        if (isUser) Color.LightGray
        else Color.LightGray

    // alignment: user on the right, bot on the left
    val horizontalAlignment =
        if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {
        // the bubble itself
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp) // don't let it stretch full width
                .background(
                    color = bubbleColor,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            if (isMarkdown) {
                MarkdownViewer(
                    markdownText = text,
                    timestamp = timestamp, // we'll ignore timestamp inside viewer now
                    modifier = Modifier.fillMaxWidth(),
                    color = textColor
                )
            } else {
                Text(
                    text = text,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp,
                    modifier = if (loading) modifier.shimmer() else modifier
                )
            }
        }

        Row(
            modifier = modifier.widthIn(max = 280.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // timestamp under bubble
            if (isMarkdown) {
                Text(
                    text = formatTimestamp(timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .padding(top = 4.dp, start = 4.dp, end = 10.dp)
                )
            }

            if (!loading) {
                IconButton(
                    onClick = {
                        onClick()
                    },
                    modifier = modifier
                        .size(15.dp)
                        .padding(start = 3.dp, end = 7.dp)
                ) {
                    Icon(Icons.Default.ContentPaste, contentDescription = "Localized description")
                }
            }
        }
    }
}

@Composable
fun ChatImageBubble(
    imageUri: String,
    timestamp: Long,
    isUser: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val bubbleColor = Color.DarkGray
    val horizontalAlignment =
        if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 220.dp) // slightly narrower
                .background(
                    color = bubbleColor,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(8.dp)
        ) {
            AsyncImage(
                model = Uri.parse(imageUri),
                contentDescription = "user attachment",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.Black, RoundedCornerShape(12.dp))
            )
        }

    }
}


@Composable
fun MarkdownViewer(
    markdownText: String,
    timestamp: Long,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    val parsedContent = parseMarkdown(markdownText, color,timestamp)

    Column(modifier = modifier) {
        parsedContent.forEach { component ->
            component()
        }
    }
}

@Composable
fun ChatMessageRow(msg: ChatMessage, modifier: Modifier = Modifier,context: Context) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {

        if (msg.imageUri != null) {
            ChatImageBubble(
                imageUri = msg.imageUri,
                timestamp = msg.timestamp,
                isUser = true,
                onClick = {
                    // maybe later: open fullscreen, copy, etc.
                }
            )
            Spacer(modifier = modifier.height(6.dp))
        }

        ChatBubble(
            text = msg.prompt,
            timestamp = msg.timestamp,
            isUser = true,
            isMarkdown = false,
            onClick = {
                copyToClipboard(context = context, text = msg.prompt)
            }
        )

        Spacer(modifier = modifier.height(8.dp))



        ChatBubble(
            text = msg.answer,
            timestamp = msg.timestamp,
            isUser = false,
            isMarkdown = true,
            onClick = {
                copyToClipboard(context = context, text = msg.answer)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))


    }
}


@Composable
fun ElevatedCardExample(modifier: Modifier = Modifier, image : Painter = painterResource(R.drawable.ic_marker) ) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 15.dp, start = 15.dp)
    ) {
        Row(modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround){
            Icon(
                tint = Color.Unspecified,
                painter = image,
                contentDescription = "",
                modifier = modifier
                    .padding(10.dp))
            Text(
                text = "Remember what the user entered in former enquiries",
                modifier = modifier
                    .padding(10.dp),
                textAlign = TextAlign.Start,
                fontSize = 12.sp
            )
        }
    }
}


@Composable
fun PromptInputField(
    loading : Boolean,
    prompt : String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = if (loading) "" else prompt,
        onValueChange = {
            onValueChange(it)
        },
        label = {
            Text(
                "Ask me anything...",
                modifier = modifier.clip(RoundedCornerShape(25.dp))
            )
        },
        modifier = modifier,
        shape = RoundedCornerShape(25.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.LightGray,
            unfocusedContainerColor = Color.LightGray,
            focusedLabelColor = Color.Gray,
            unfocusedLabelColor = Color.Gray,
            disabledLabelColor = Color.Gray,
            cursorColor = Color.Black
        ),
    )
}

@Composable
fun ImagePickerMenu(
    selectedImages: Uri?,
    expanded: Boolean,
    onCancelClicked: () -> Unit,
    toggleExpanded: () -> Unit,
    launchCamera: () -> Unit,
    launchPhotoPicker: () -> Unit
) {
    IconButton(onClick = {
        toggleExpanded()
    }) {
        if (selectedImages != null) {
            AsyncImage(
                model = selectedImages,
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
        } else {
            Icon(
                Icons.Filled.Image,
                contentDescription = "Localized description",
                tint = colorResource(R.color.orange)
            )
        }
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { toggleExpanded() }
    ) {
        DropdownMenuItem(
            text = { Text("Cancel") },
            onClick = {
                onCancelClicked()
            },
            leadingIcon = {
                Icon(Icons.Default.Cancel, contentDescription = "Localized description")
            }
        )
        DropdownMenuItem(
            text = { Text("Camera") },
            onClick = {
                toggleExpanded()
                launchCamera()
            },
            leadingIcon = {
                Icon(Icons.Default.CameraAlt, contentDescription = "Localized description")
            }
        )
        DropdownMenuItem(
            text = { Text("Gallery") },
            onClick = {
                toggleExpanded()
                launchPhotoPicker()
            },
            leadingIcon = {
                Icon(Icons.Default.Image, contentDescription = "Localized description")
            }
        )
    }
}
