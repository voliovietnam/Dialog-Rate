package com.volio.vn.data.repositories.background

import com.volio.vn.data.entities.DataState
import com.volio.vn.data.models.background.BackgroundCategoryModel
import com.volio.vn.data.models.background.BackgroundModel
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Query

interface BackgroundRepository {
    suspend fun getAllCategoryBackground(
        idParent: String,
        isSynAll: Boolean
    ): Flow<DataState<List<BackgroundCategoryModel>>>

    suspend fun getAllDataBackground(
        categoryId: String
    ): Flow<DataState<List<Pair<BackgroundCategoryModel, List<BackgroundModel>>>>>

    suspend fun getAllItemBackground(
        @Query("category_id") idCategory: String,
        isSynAll: Boolean
    ): Flow<DataState<List<BackgroundModel>>>

    suspend fun downloadImage(backgroundModel: BackgroundModel): Flow<DataState<String>>
}