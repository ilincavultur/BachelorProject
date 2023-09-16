package com.example.digitaldiaryba.ui.components.topbar

import androidx.compose.foundation.layout.*

import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

import com.example.digitaldiaryba.util.Constants.TITLE_FONT_SIZE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    title: String?,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.onSurface,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    TopAppBar(
        title = {
            title?.let {
                Text(
                    text = it,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = TITLE_FONT_SIZE),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        backgroundColor = backgroundColor,
        contentColor = contentColor
    )
}