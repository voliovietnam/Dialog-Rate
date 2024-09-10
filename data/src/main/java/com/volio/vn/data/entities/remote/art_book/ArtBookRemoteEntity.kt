package com.volio.vn.data.entities.remote.art_book

import com.google.gson.annotations.SerializedName

data class ArtBookRemoteEntity(
    val id: String,

    @SerializedName("name")
    val artBookName: String,

    @SerializedName("photo")
    val artBookUrl: String?,

    val priority: Long,

    @SerializedName("is_pro")
    val isPro: Boolean?,

    @SerializedName("custom_fields")
    val customFieldsEntity: ArtBookCustomFields?

)
