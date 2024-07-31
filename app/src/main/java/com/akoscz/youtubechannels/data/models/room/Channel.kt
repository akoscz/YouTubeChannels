package com.akoscz.youtubechannels.data.models.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.akoscz.youtubechannels.data.models.api.ChannelSearchItem

/**
 * This class represents a channel in the database.
 * Channels are stored in the "channels" table.
 *
 * The "channelDetailsId" column in the "channels" table references the "id" column in the "channel_details" table
 * and is used to link the channel to its details.  This can be null if the channel details have not been
 * fetched yet.
 *
 */
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

/**
 * Helper function to map a ChannelSearchItem to a Channel entity.
 */
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