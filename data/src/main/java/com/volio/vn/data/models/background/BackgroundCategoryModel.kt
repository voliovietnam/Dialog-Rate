package com.volio.vn.data.models.background

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BackgroundCategoryModel(
    val id: String,
    val backgroundName: String,
    val backgroundCategoryUrl: String,
    val priority: Long,
    val isPro: Boolean,
    val sound: String=""
) : Parcelable
