package com.volio.vn.data.entities.remote.dummy

import com.google.gson.annotations.SerializedName

data class DummyRemoteEntity(
    val id: String,

    @SerializedName("category_id")
    val idCategory: String,

    val priority: Long,

    val name: String,

    val thumbnail: String = "",

    val photo: String ="",

    val status: Boolean?,

    @SerializedName("is_pro")
    val isPro: Boolean?,

    @SerializedName("is_new")
    val isNew: Boolean?,

    @SerializedName("custom_fields")
    val customFieldsEntity: DummyCustomFieldsEntity?
)
