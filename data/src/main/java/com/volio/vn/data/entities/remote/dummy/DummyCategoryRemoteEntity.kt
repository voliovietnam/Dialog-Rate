package com.volio.vn.data.entities.remote.dummy

import com.google.gson.annotations.SerializedName

data class DummyCategoryRemoteEntity(
    val id: String,

    @SerializedName("parent_id")
    val parentId: String,

    val priority: Long,

    val name: String,

    val thumbnail: String?,

    val photo: String?,

    val status: Boolean?,

    @SerializedName("is_pro")
    val isPro: Boolean?,

    @SerializedName("is_new")
    val isNew: Boolean?,
)
