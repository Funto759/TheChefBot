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
fun showAlertDialog(
    onDismissRequest : () -> Unit
    , sessionToDelete: Int?
    , onConfirm: () -> Unit,
    onCancel: () -> Unit,) {
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
//            showDeleteDialog = false
//            sessionToDelete = null
        },
        title = { Text("Delete Chat?") },
        text = {
            Text(
                if (sessionToDelete == null)
                    "Are you sure you want to delete all chat sessions?"
                else
                    "Are you sure you want to delete this chat session?"
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
//                    if (sessionToDelete != null) {
//                        viewModel.deleteSession(sessionToDelete!!)
//                    } else {
//                        viewModel.deleteAllSessions()
//                    }
//                    showDeleteDialog = false
//                    sessionToDelete = null
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancel()
//                    showDeleteDialog = false
//                    sessionToDelete = null
                }
            ) {
                Text("Cancel")
            }
        }
    )
}
