package com.akoscz.youtubechannels.data.models.api

data class Thumbnails(
    val default: Thumbnail,
    val medium: Thumbnail,
    val high: Thumbnail,
    val standard: Thumbnail? = null,
    val maxres: Thumbnail? = null
)

data class Thumbnail(
    val url: String,
    val width: Int? = 0,
    val height: Int? = 0
)
