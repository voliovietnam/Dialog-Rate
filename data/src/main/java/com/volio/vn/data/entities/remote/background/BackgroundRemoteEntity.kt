package com.volio.vn.data.entities.remote.background

import com.google.gson.annotations.SerializedName

data class BackgroundRemoteEntity(
    val id: String = "",
    @SerializedName("is_new")
    val isNew: Boolean = false,
    @SerializedName("is_pro")
    val isPro: Boolean = false,
    val name: String = "",
    @SerializedName("parent_id")
    val parentId: String = "",
    val photo: String = "",
    val priority: Long,
    val status: Boolean = false,

    @SerializedName("custom_fields")
    val backgroundCustomFieldsEntity: BackgroundCustomField?
)