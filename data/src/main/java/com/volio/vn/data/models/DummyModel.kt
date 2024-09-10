package com.volio.vn.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DummyModel(
    val id: String,
    val idCategory: String,
    val priority: Long,
    val remotePath: String,
    val localPath: String,
) : Parcelable
