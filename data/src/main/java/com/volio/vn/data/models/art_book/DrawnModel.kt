package com.volio.vn.data.models.art_book

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DrawnModel(
    val id: String,
    val artBookId: String,
    val priority: Long,
    val name: String,
    val isPro: Boolean,
    val zipData: String,
    val originalDrawn: String,
    val bordersDrawn: String,
    val targetDrawn: String,
    val drawnPaths: List<String>,
): Parcelable
