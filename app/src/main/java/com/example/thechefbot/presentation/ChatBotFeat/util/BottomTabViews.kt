package com.example.thechefbot.presentation.ChatBotFeat.util

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.thechefbot.R




@Composable
fun ImagePickerMenu(
    modifier: Modifier = Modifier,
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
                Icon(Icons.Default.Cancel, contentDescription = "Localized description",
                    modifier = modifier.padding(5.dp),tint = colorResource(R.color.orange))
            }
        )
        DropdownMenuItem(
            text = { Text("Camera") },
            onClick = {
                toggleExpanded()
                launchCamera()
            },
            leadingIcon = {
                Icon(Icons.Default.CameraAlt, contentDescription = "Localized description"
                 ,modifier = modifier.padding(5.dp),tint = colorResource(R.color.orange))
            }
        )
        DropdownMenuItem(
            text = { Text("Gallery") },
            onClick = {
                toggleExpanded()
                launchPhotoPicker()
            },
            leadingIcon = {
                Icon(Icons.Default.Image, contentDescription = "Localized description"
                    ,modifier = modifier.padding(5.dp),tint = colorResource(R.color.orange))
            }
        )
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
            focusedBorderColor = colorResource(R.color.pink),
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