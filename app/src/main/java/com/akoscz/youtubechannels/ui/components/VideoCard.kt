package com.akoscz.youtubechannels.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akoscz.youtubechannels.data.models.Video

@Composable
fun VideoCard(video: Video) {
    // Implement your VideoCard layout here to display video details
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = video.title)
            // ... other video details
        }
    }
}