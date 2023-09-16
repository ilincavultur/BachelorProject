package com.example.digitaldiaryba.ui.components.dropdown_menu

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// START https://alexzh.com/jetpack-compose-dropdownmenu/
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DropdownMenuFoursquareApi(
    onClick: (String) -> Unit
) {
    val context = LocalContext.current
    val options = mapOf<String, String>(Pair("Landmarks and Outdoors", "16000"), Pair("Arts and Entertainment", "10000"), Pair("Dining and Drinking", "13000"), Pair("Travel and Transportation", "19000"))
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("Landmarks and Outdoors") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    content = {
                        Text(text = item.key)
                    },
                    onClick = {
                        onClick(item.value)
                        selectedText = item.key
                        expanded = false
                        //Toast.makeText(context, item.key, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
// END https://alexzh.com/jetpack-compose-dropdownmenu/