package com.example.digitaldiaryba.ui.components.card.media_object_card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.digitaldiaryba.R

@Composable
fun EnterDescriptionSurface(
    description: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(width = 300.dp, height = 100.dp)
            .border(
                BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.onSecondary),
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(10.dp)
            ),
        shape = RoundedCornerShape(10.dp)
    ) {

        Box(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier

            ) {
                Text(
                    text = description,
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                IconButton(onClick = {
                    onClick()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_edit_24),
                        contentDescription = "edit"
                    )
                }


            }
        }

    }
}