package com.volio.vn.data.entities.remote

data class BaseResponse<T>(
    val data: List<T>,

    val message: String,

    val metadata: MetadataResponse,

    val status: Long,
)
