package com.volio.vn.data.service.api

import com.volio.vn.data.Constants.CATEGORIES
import com.volio.vn.data.Constants.ITEMS_ALL
import com.volio.vn.data.Constants.ITEMS
import com.volio.vn.data.entities.remote.BaseResponse
import com.volio.vn.data.entities.remote.art_book.ArtBookRemoteEntity
import com.volio.vn.data.entities.remote.background.BackgroundRemoteEntity
import com.volio.vn.data.entities.remote.drawn.DrawnRemoteEntity
import com.volio.vn.data.entities.remote.dummy.DummyCategoryRemoteEntity
import com.volio.vn.data.entities.remote.dummy.DummyRemoteEntity
import retrofit2.http.GET
import retrofit2.http.Query

interface SampleApi {

    @GET(ITEMS)
    suspend fun getDummyByIdDummyCategory(
        @Query("category_id") category: String,
        @Query("offset") offset: Int,
        @Query("limit") size: Int
    ): BaseResponse<DummyRemoteEntity>

    @GET(CATEGORIES)
    suspend fun getAllDummyCategory(
        @Query("parent_id") category: String,
    ): BaseResponse<DummyCategoryRemoteEntity>

    @GET(CATEGORIES)
    suspend fun getAllArtBook(
        @Query("parent_id") category: String,
    ): BaseResponse<ArtBookRemoteEntity>

    @GET(ITEMS)
    suspend fun getDrawnByIdArtBook(
        @Query("category_id") category: String,
        @Query("offset") offset: Int,
        @Query("limit") size: Int
    ): BaseResponse<DrawnRemoteEntity>

    @GET(CATEGORIES)
    suspend fun getAllCategoryBackground(
        @Query("parent_id") categoryId: String
    ): BaseResponse<BackgroundRemoteEntity>

    @GET(ITEMS)
    suspend fun getItemBackgroundByIdCategory(
        @Query("category_id") idCategory: String,
        @Query("limit") limit: Int
    ): BaseResponse<BackgroundRemoteEntity>

    @GET(ITEMS_ALL)
    suspend fun getAllItemBackground(
        @Query("category_id") idCategory: String
    ): BaseResponse<BackgroundRemoteEntity>
}
