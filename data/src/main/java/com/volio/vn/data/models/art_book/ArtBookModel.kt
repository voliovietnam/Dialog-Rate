package com.volio.vn.data.models.art_book

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArtBookModel(
    val id: String,
    val artBookName: String,
    val artBookUrl: String,
    val priority: Long,
    val isPro: Boolean,
    val urlSound: String,
) : Parcelable
