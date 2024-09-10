package com.volio.vn.data.models.art_book

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class DrawnSave(
    val id: String = UUID.randomUUID().toString(),
    val idOrigin: String,
    val categoryNameParent: String,
    val pathSave: String,
    val categoryName: String,
    val timeSave: Long = System.currentTimeMillis()
) : Parcelable
