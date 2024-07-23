package com.akoscz.youtubechannels.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Channel(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val thumbnailDefaultUrl: String,
    val thumbnailMediumUrl: String,
    val thumbnailHighUrl: String
)