package com.example.thechefbot.presentation.ChatBotFeat.util

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.thechefbot.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopChefBar(
    modifier: Modifier = Modifier
               , scrollBehavior: TopAppBarScrollBehavior
    , onClick: () -> Unit
    ,expanded: Boolean,
    onCancelClicked: () -> Unit,
    toggleExpanded: () -> Unit,
    text: String?) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = if (text.isNullOrEmpty()) "Recipe Generator" else text)
        },
        actions = {
            IconButton(onClick = {
                toggleExpanded()
            }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "",
                    tint = colorResource(R.color.orange)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { toggleExpanded() }
            ) {
                DropdownMenuItem(
                    text = { Text("Settings") },
                    onClick = {
                        onCancelClicked()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person
                            , contentDescription = "Localized description"
                            ,modifier = modifier.padding(5.dp)
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete Chat") },
                    onClick = {
                        toggleExpanded()
//                        launchCamera()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Delete, contentDescription = "Localized description"
                            ,modifier = modifier.padding(5.dp))
                    }
                )
                DropdownMenuItem(
                    text = { Text("Toggle Mode") },
                    onClick = {
                        toggleExpanded()
//                        launchPhotoPicker()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.InvertColors, contentDescription = "Localized description"
                        ,modifier = modifier.padding(5.dp))
                    }
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