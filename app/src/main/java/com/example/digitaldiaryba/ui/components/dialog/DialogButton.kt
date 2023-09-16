package com.example.digitaldiaryba.ui.components.dialog

import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.example.digitaldiaryba.util.enums.EDialogButtonType

@Composable
fun DialogButton(
    type: EDialogButtonType,
    onClickAction: () -> Unit,
) {
    ExtendedFloatingActionButton(
        onClick = onClickAction,
        text = {
            Text(
                text = when(type) {
                    EDialogButtonType.CANCEL -> "Cancel"
                    EDialogButtonType.ADD -> "Add"
                    EDialogButtonType.CREATE -> "Create"
                                  },
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
            )
        },
        backgroundColor =
            when(type) {
                EDialogButtonType.CANCEL -> {
                    androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                }
                EDialogButtonType.ADD, EDialogButtonType.CREATE -> {
                    androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                }
            },
    )
}