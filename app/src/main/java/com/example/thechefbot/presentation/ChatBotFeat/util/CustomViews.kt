package com.example.thechefbot.presentation.ChatBotFeat.util

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.thechefbot.presentation.ChatBotFeat.data.ChatMessage
import com.example.thechefbot.presentation.ChatBotFeat.data.ChatSession
import com.example.thechefbot.util.CommonUtil.copyToClipboard
import com.example.thechefbot.util.CommonUtil.formatTimestamp
import com.example.thechefbot.util.CommonUtil.parseMarkdown
import com.example.thechefbot.util.shimmer


















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
        if (isUser) colorResource(R.color.pink)
        else Color.DarkGray

    val textColor =
        if (isUser) Color.DarkGray
        else Color.LightGray

    // alignment: user on the right, bot on the left
    val horizontalAlignment =
        if (isUser) Alignment.End else Alignment.Start

    val bubbleWidthModifier =
        if (loading) modifier.fillMaxWidth(0.9f)   // 90% of screen while loading
        else Modifier.widthIn(max = 280.dp)

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {
        // the bubble itself
        Box(
            modifier = modifier
                .widthIn(max = 280.dp)
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
            when {
                loading -> {
                    // full-width shimmering block (matches bubble width)
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = 20.dp) // at least one line tall
                            .shimmer(cornerRadius = 12.dp)
                    )
                }
                isMarkdown -> {
                    MarkdownViewer(
                        markdownText = text,
                        timestamp = timestamp,
                        modifier = Modifier.fillMaxWidth(),
                        color = textColor
                    )
                }
                else -> {
                    Text(
                        text = text,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                }
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
                    onClick = onClick,
                    modifier = modifier
                        .size(22.dp)
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
    isUser: Boolean,
    modifier: Modifier = Modifier,
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
            modifier = modifier
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
                modifier = modifier
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
fun ChatMessageRow(
    msg: ChatMessage
    ,showAlertDialog: Boolean = false
    , modifier: Modifier = Modifier
    ,context: Context
    ,session: ChatSession?,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {

        if (msg.imageUri != null) {
            ChatImageBubble(
                imageUri = msg.imageUri,
                isUser = true,
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
            modifier = modifier,
            onClick = {
                copyToClipboard(context = context, text = msg.answer)
            }
        )

        Spacer(modifier = modifier.height(8.dp))


    }

}
}


@Composable
fun ElevatedCardExample(modifier: Modifier = Modifier, image : Painter = painterResource(R.drawable.ic_marker) ) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = modifier
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


