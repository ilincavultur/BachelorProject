package com.example.digitaldiaryba.ui.components.card.media_object_card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.digitaldiaryba.R

@Composable
fun NoDetailsCardContent(
    onTextClick: () -> Unit,
    onDeleteIconClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.onPrimary
                )
        ) {
            Text(
                text = "See Details",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .clickable {
                        onTextClick()
                    }
                    .align(
                        Alignment.CenterVertically
                    )
                    .padding(start = 10.dp)
            )
            IconButton(
                onClick = onDeleteIconClick
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_delete
                    ),
                    contentDescription = "delete_btn",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}