package com.akoscz.youtubechannels.data.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ChannelDetails (
    @PrimaryKey val id: String, // This should match the Channel's id
    val viewCount: String,
    val subscriberCount: String
)