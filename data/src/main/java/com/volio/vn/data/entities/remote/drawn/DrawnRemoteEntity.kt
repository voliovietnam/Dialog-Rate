package com.volio.vn.data.entities.remote.drawn

import com.google.gson.annotations.SerializedName

data class DrawnRemoteEntity(
    val id: String,

    @SerializedName("category_id")
    val artBookId: String,

    val priority: Long,

    val name: String,

    @SerializedName("is_pro")
    val isPro: Boolean?,

    @SerializedName("photo")
    val originalDrawn: String="",

    @SerializedName("custom_fields")
    val customFieldsEntity: DrawnCustomFields?
)
