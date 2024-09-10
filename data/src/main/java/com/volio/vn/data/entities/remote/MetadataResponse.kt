package com.volio.vn.data.entities.remote

import com.google.gson.annotations.SerializedName

data class MetadataResponse(
    val limit: Long,

    val offset: Long,

    val length: Long,

    @SerializedName("is_last")
    val isLast: Boolean,

    val path: String,
)