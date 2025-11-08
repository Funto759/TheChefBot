package com.example.thechefbot.presentation.ChatBotFeat.util

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.thechefbot.presentation.ChatBotFeat.model.RecipeViewModel

@Composable
fun ConfirmDeleteDialog(
    sessionToDelete: Int?,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {

    val isDeleteAll = sessionToDelete == null || sessionToDelete == 0
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Delete Chat?") },
        text = {
            Text(
                if (isDeleteAll) {
                    "Are you sure you want to delete all chat sessions?"
                } else{
                    "Are you sure you want to delete this chat session?"
                }
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
