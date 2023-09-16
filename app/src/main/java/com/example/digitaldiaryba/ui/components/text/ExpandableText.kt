package com.example.digitaldiaryba.ui.components.text

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.digitaldiaryba.R

// START https://proandroiddev.com/expandabletext-in-jetpack-compose-b924ea424774
@Composable
fun ExpandingText(modifier: Modifier = Modifier, text: String) {
    var isExpanded by remember { mutableStateOf(false) }
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    var isClickable by remember { mutableStateOf(false) }
    var finalText by remember { mutableStateOf(text) }

    val textLayoutResult = textLayoutResultState.value
    LaunchedEffect(textLayoutResult) {
        if (textLayoutResult == null) return@LaunchedEffect

        when {
            isExpanded -> {
                finalText = text
            }
            !isExpanded && textLayoutResult.hasVisualOverflow -> {
                val adjustedText = text.substringBefore("\n")

                finalText = adjustedText

                isClickable = true
            }
        }
    }

    Text(
        text = finalText,
        style = TextStyle(
            color = if (isExpanded) colorResource(id = R.color.accent_purple) else Color.Gray,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Default
        ),
        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
        onTextLayout = { textLayoutResultState.value = it },
        modifier = modifier
            .clickable(enabled = isClickable) { isExpanded = !isExpanded }
            .animateContentSize(),
    )
}
// END https://proandroiddev.com/expandabletext-in-jetpack-compose-b924ea424774