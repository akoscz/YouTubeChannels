package com.akoscz.youtubechannels.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(searchText: String,
              onSearchTextChanged: (String) -> Unit,
              onSearchButtonClicked: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = searchText,
            onValueChange = { onSearchTextChanged (it) },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Search for channels") }
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                onSearchButtonClicked(searchText)
                focusManager.clearFocus() // Dismiss the keyboard
            }
        ) {
            Icon(Icons.Filled.Search, contentDescription = "Search")
        }
    }
}