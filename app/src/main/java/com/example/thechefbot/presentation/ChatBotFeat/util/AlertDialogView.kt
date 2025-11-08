package com.example.thechefbot.presentation.ChatBotFeat.util

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.thechefbot.presentation.AuthFeat.util.EditableView
import com.example.thechefbot.presentation.ChatBotFeat.model.RecipeViewModel

@Composable
fun ConfirmDeleteDialog(
    sessionToDelete: Int?,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Delete Chat?") },
        text = {
            Text(
                text ="Are you sure you want to perform this action?"
            )
        },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )) { Text("Delete") }
        },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancel") } }
    )
}

@Composable
fun ChangeTitleDialog(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Rename Chat?") },
        text = {
            EditableView(
                modifier = modifier,
                value = value,
                hint = "Email",
                onValueChange = onValueChange
            )
        },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )) { Text("Rename") }
        },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancel") } }
    )
}
