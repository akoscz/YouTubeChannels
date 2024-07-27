package com.akoscz.youtubechannels.data.models.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "channels")
data class Channel(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val thumbnailDefaultUrl: String,
    val thumbnailMediumUrl: String,
    val thumbnailHighUrl: String,
    // Add index for efficient lookups
    @ColumnInfo(index = true) val channelDetailsId: String? = null
)