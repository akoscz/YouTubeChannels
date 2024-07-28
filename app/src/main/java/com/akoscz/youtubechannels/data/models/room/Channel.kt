package com.akoscz.youtubechannels.data.models.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.akoscz.youtubechannels.data.models.api.ChannelSearchItem

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

fun mapToChannel(channelSearchItem: ChannelSearchItem): Channel {
    return Channel(
        id = channelSearchItem.id.channelId,
        title = channelSearchItem.snippet.title,
        description = channelSearchItem.snippet.description,
        thumbnailDefaultUrl = channelSearchItem.snippet.thumbnails.default.url,
        thumbnailMediumUrl = channelSearchItem.snippet.thumbnails.medium.url,
        thumbnailHighUrl = channelSearchItem.snippet.thumbnails.high.url,
        channelDetailsId = ""
    )
}