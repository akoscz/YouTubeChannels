package com.akoscz.youtubechannels.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

// In the data/models package
@Entity
data class Channel(
    @PrimaryKey val id: String,
    val title: String,
    val thumbnailUrl: String? = null // Add other relevant fields asneeded
)