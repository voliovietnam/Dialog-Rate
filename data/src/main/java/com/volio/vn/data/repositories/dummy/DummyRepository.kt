package com.volio.vn.data.repositories.dummy

import com.volio.vn.data.entities.DataState
import com.volio.vn.data.models.DummyCategoryModel
import com.volio.vn.data.models.DummyModel
import kotlinx.coroutines.flow.Flow

interface DummyRepository {
    suspend fun getAllDummyCategory(): Flow<DataState<List<DummyCategoryModel>>>
    suspend fun getDummyByCategoryId(
        categoryId: String
    ): Flow<DataState<List<DummyModel>>>

    suspend fun downloadDummyZip(dummyModel: DummyModel): Flow<DataState<DummyModel>>
}