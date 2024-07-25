package com.akoscz.youtubechannels.data.models.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Channel(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val thumbnailDefaultUrl: String,
    val thumbnailMediumUrl: String,
    val thumbnailHighUrl: String,
    @ColumnInfo(index = true) // Add index for efficient lookups
    val channelDetailsId: String? = null
)