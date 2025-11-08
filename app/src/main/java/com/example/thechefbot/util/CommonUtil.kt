package com.example.thechefbot.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.prefs.Preferences



object CommonUtil {

    fun formatTimestamp(ts: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault())
        return sdf.format(Date(ts))
    }
    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Recipe", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun getBitmapFromUri(context: Context, uri: Uri?): Bitmap? {
        if (uri != null) {
            var inputStream: InputStream? = null
            return try {
                inputStream = context.contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                inputStream?.close()
            }
        } else {
            Toast.makeText(context, "Image cannot be empty", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    fun parseMarkdown(text: String, color: Color, timestamp: Long): List<@Composable () -> Unit> {
        val parsedComponents = mutableListOf<@Composable () -> Unit>()

        val lines = text.split("\n")

        lines.forEach { line ->
            when {
                line.startsWith("# ") -> {
                    parsedComponents.add {
                        Text(
                            text = line.removePrefix("# "),
                            color = color,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Bold,
                            style = androidx.compose.ui.text.TextStyle(
                                fontSize = 24.sp
                            )
                        )
                    }
                }

                line.contains("**") -> {
                    parsedComponents.add {
                        val content = line.substringAfter("**").substringBeforeLast("**")
                        Text(
                            text = content,
                            lineHeight = 20.sp,
                            color = color,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                line.contains("*") -> {
                    parsedComponents.add {
                        val content = line.substringAfter("*").substringBeforeLast("*")
                        Text(
                            text = content,
                            lineHeight = 20.sp,
                            color = color,
                            style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        )
                    }
                }

                else -> {
                    parsedComponents.add {
                        Text(text = line, color = color, lineHeight = 20.sp,)
                    }
                }
            }
        }


        return parsedComponents
    }
}