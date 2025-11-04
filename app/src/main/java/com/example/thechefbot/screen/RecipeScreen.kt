package com.example.thechefbot.screen

import android.R.attr.textColor
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.thechefbot.R

import com.example.thechefbot.ui.theme.TheChefBotTheme
import com.example.thechefbot.util.CommonUtil.copyToClipboard
import com.example.thechefbot.util.CommonUtil.parseMarkdown
import com.example.thechefbot.util.shimmer
import com.google.firebase.platforminfo.DefaultUserAgentPublisher.component
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.util.UUID
import kotlin.text.insert

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreenTest() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()



    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))

                    IconButton(onClick = {
                        scope.launch {
                            if (drawerState.isClosed) {
                                drawerState.open()
                            } else {
                                drawerState.close()
                            }
                        }
                    }) {
                        Icon(Icons.Default.MenuOpen, contentDescription = "Menu")
                    }

                    Image(
                        painter = painterResource(R.drawable.ic_sun),
                        contentDescription = "user attachment",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.clip(CircleShape).align(Alignment.CenterHorizontally)
                    )
                    Text(
                        "Funmito",
                        modifier = Modifier.padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.titleMedium
                    )
                    HorizontalDivider()

                    NavigationDrawerItem(
                        modifier = Modifier.padding(16.dp)
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
                        onClick = { /* Handle click */ }
                    )
                    NavigationDrawerItem(
                        modifier = Modifier.padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(16.dp),
                        label = { Text("Delete Chat") },
                        selected = false,
                        icon = {
                            Icon(
                                painterResource(R.drawable.ic_chat_del),
                                contentDescription = null
                            )
                        },
                        onClick = { /* Handle click */ }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        "Chat History",
                        modifier = Modifier.padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.titleMedium
                    )
                    NavigationDrawerItem(
                        modifier = Modifier.padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(16.dp),
                        label = { Text("Settings") },
                        selected = false,
                        icon = { Icon(painterResource(R.drawable.ic_chat_options), contentDescription = null) },
                        onClick = { /* Handle click */ }
                    )
                    NavigationDrawerItem(
                        modifier = Modifier.padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(16.dp),
                        label = { Text("Help and feedback") },
                        selected = false,
                        icon = {
                            Icon(
                                painterResource(R.drawable.ic_chat_options),
                                contentDescription = null
                            )
                        },
                        onClick = { /* Handle click */ },
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Navigation Drawer Example") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            ChatMessageRow(paddingValues = paddingValues)
        }

    }
}

@Composable
fun ModalDrawerViewMain(modifier: Modifier = Modifier, composable : @Composable (() -> Unit), drawerState: DrawerState) {

}

@Composable
fun ChatMessageRow(modifier: Modifier = Modifier, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp).padding(paddingValues = paddingValues)
    ) {

        ChatBubble(
            text = "How do i make an egusi meal",
            timestamp = 11,
            isUser = true,
            isMarkdown = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        ChatBubble(
            text = "8. Summary\n" +
                    "\n" +
                    "You're basically building a mini ChatGPT:\n" +
                    "\n" +
                    "Tables\n" +
                    "\n" +
                    "ChatSession(sessionId, title, lastUsedTimeStamp)\n" +
                    "\n" +
                    "ChatMessage(messageId, sessionOwnerId, prompt, answer, timestamp)\n" +
                    "\n" +
                    "DAOs\n" +
                    "\n" +
                    "ChatSessionDao to create/update/list sessions.\n" +
                    "\n" +
                    "ChatMessageDao to insert and load messages for a given session.\n" +
                    "\n" +
                    "Repository\n" +
                    "\n" +
                    "Knows how to:\n" +
                    "\n" +
                    "create or reuse the current session,\n" +
                    "\n" +
                    "insert messages,\n" +
                    "\n" +
                    "bump session recency.\n" +
                    "\n" +
                    "ViewModel\n" +
                    "\n" +
                    "Holds the active session,\n" +
                    "\n" +
                    "Exposes:\n" +
                    "\n" +
                    "allSessions (for recents screen),\n" +
                    "\n" +
                    "messagesForActiveSession (for chat screen),\n" +
                    "\n" +
                    "After every successful AI response:\n" +
                    "\n" +
                    "save prompt+answer to DB under that session.\n" +
                    "\n" +
                    "UI\n" +
                    "\n" +
                    "A “session list” screen like an inbox,\n" +
                    "\n" +
                    "A “chat screen” that streams messages for the chosen session.\n" +
                    "\n" +
                    "This structure gives you exactly what you asked for:\n" +
                    "\n" +
                    "“when i ask a prompt question the question, answer and timestamp should be saved to a db, that can be called to provide a list of all the prompts and answers made under that particular history”",
            timestamp = 11,
            isUser = false,
            isMarkdown = true
        )


        Spacer(modifier = Modifier.height(8.dp))


    }
}

//@Composable
//fun ChatBubble(
//    text: String,
//    timestamp: Long,
//    isUser: Boolean,
//    isMarkdown: Boolean,
//    modifier: Modifier = Modifier,
//    loading : Boolean = false
//) {
//    // colors
//    val bubbleColor =
//        if (isUser) Color.DarkGray
//        else Color.DarkGray
//
//    val textColor =
//        if (isUser) Color.LightGray
//        else Color.LightGray
//
//    // alignment: user on the right, bot on the left
//    val horizontalAlignment =
//        if (isUser) Alignment.End else Alignment.Start
//
//    Column(
//        modifier = modifier
//            .fillMaxWidth(),
//        horizontalAlignment = horizontalAlignment
//    ) {
//        // the bubble itself
//        Box(
//            modifier = Modifier
//                .widthIn(max = 280.dp) // don't let it stretch full width
//                .background(
//                    color = bubbleColor,
//                    shape = RoundedCornerShape(
//                        topStart = 16.dp,
//                        topEnd = 16.dp,
//                        bottomStart = if (isUser) 16.dp else 4.dp,
//                        bottomEnd = if (isUser) 4.dp else 16.dp
//                    )
//                )
//                .padding(12.dp)
//        ) {
//            if (isMarkdown) {
//                MarkdownViewer(
//                    markdownText = text,
//                    timestamp = timestamp, // we'll ignore timestamp inside viewer now
//                    modifier = Modifier.fillMaxWidth(),
//                    color = textColor
//                )
//            } else {
//                Text(
//                    text = text,
//                    color = textColor,
//                    style = MaterialTheme.typography.bodyMedium,
//                    lineHeight = 20.sp,
//                    modifier = if (loading) modifier.shimmer() else modifier
//                )
//            }
//        }
//
//        Row(
//            modifier = modifier.widthIn(max = 280.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            // timestamp under bubble
////            if (isMarkdown) {
//                Text(
//                    text = formatTimestamp(timestamp),
//                    style = MaterialTheme.typography.labelSmall,
//                    color = MaterialTheme.colorScheme.outline,
//                    modifier = Modifier
//                        .padding(top = 4.dp, start = 4.dp, end = 10.dp)
//                )
////            }
//
//            IconButton(onClick = {
//
//            },
//                modifier = modifier.size(25.dp).padding(start = 3.dp, end = 7.dp)) {
//                Icon(Icons.Default.ContentPaste, contentDescription = "Localized description")
//            }
//        }
//    }
//}



@Preview
@Composable
fun preview(){
    TheChefBotTheme {
        RecipeScreenTest()
    }
}


