package com.akoscz.youtubechannels.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Video(
    @PrimaryKey val id: String,
    val title: String
)