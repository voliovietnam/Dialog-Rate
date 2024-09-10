package com.volio.vn.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArtBookCountModel(val id: String, val count: Int) : Parcelable
