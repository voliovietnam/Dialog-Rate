package com.volio.vn.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DummyCategoryModel(
    val id: String,
    val name: String,
    val priority: Long
) : Parcelable
