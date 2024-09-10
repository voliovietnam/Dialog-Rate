package com.volio.vn.data.models.background

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BackgroundModel(
    val id: String,
    val categoryId: String,
    val name: String,
    val backgroundUrl: String,
    val priority: Long,
    val isPro: Boolean,
    val sound: String
) : Parcelable
