package com.volio.vn.data.entities.remote.dummy

import com.google.gson.annotations.SerializedName

data class DummyCustomFieldsEntity(
    @SerializedName("remote_path")
    var remote_path: String?,
)
