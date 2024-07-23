package com.akoscz.youtubechannels.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.akoscz.youtubechannels.R
import com.akoscz.youtubechannels.data.models.SearchItem
import com.akoscz.youtubechannels.ui.viewmodels.SearchChannelsViewModel


@Composable
fun ChannelSearchItemRow(result: SearchItem, viewModel: SearchChannelsViewModel = hiltViewModel()) {
    var subscribed by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        AsyncImage(
            model = result.snippet.thumbnails.default.url,
            contentDescription = "Channel Icon",
            modifier = Modifier.size(48.dp).align(Alignment.CenterVertically),
            placeholder = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with your placeholder
            error = painterResource(id = R.drawable.ic_launcher_background) // Replace with your error image
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = result.snippet.title,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = result.snippet.description,
                maxLines = 2,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = {
            viewModel.subscribeToChannel(result)
            subscribed = true
        }) {
            Text(if (subscribed) "Subscribed" else "Subscribe")
        }
    }
}
