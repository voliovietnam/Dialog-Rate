package com.volio.vn.data.repositories.drawn

import com.volio.vn.data.entities.DataState
import com.volio.vn.data.models.art_book.ArtBookModel
import com.volio.vn.data.models.art_book.DrawnModel
import kotlinx.coroutines.flow.Flow

interface DrawnRepository {
    suspend fun getArtBook(
        categoryId: String,
        isSynAll: Boolean
    ): Flow<DataState<List<ArtBookModel>>>

    suspend fun getDrawnByIdArtBook(
        categoryId: String,
        isSynAll: Boolean
    ): Flow<DataState<List<DrawnModel>>>

    suspend fun getAllDataDrawn(
        categoryId: String,
    ): Flow<DataState<List<Pair<ArtBookModel, List<DrawnModel>>>>>

    suspend fun downLoadDrawn(drawnModel: DrawnModel): Flow<DataState<DrawnModel>>
}