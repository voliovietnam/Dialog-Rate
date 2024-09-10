package com.volio.vn.data.repositories.drawn

import com.volio.vn.data.entities.remote.art_book.ArtBookRemoteEntity
import com.volio.vn.data.entities.remote.drawn.DrawnRemoteEntity
import com.volio.vn.data.mmkv.MMKVCache
import com.volio.vn.data.models.art_book.ArtBookModel
import com.volio.vn.data.models.art_book.DrawnModel

fun DrawnRepositoryImpl.syncDataArtBook(
    listResponse: MutableList<ArtBookRemoteEntity>,
    listData: MutableList<ArtBookModel>
): List<ArtBookModel> {
    val listSync = mutableListOf<ArtBookModel>()
    listResponse.forEach { category ->
        listSync.add(
            listData.find { it.id == category.id }?.copy(
                id = category.id,
                artBookName = category.artBookName,
                artBookUrl = category.artBookUrl ?: "",
                priority = category.priority,
                isPro = category.isPro ?: false,
                urlSound = category.customFieldsEntity?.sound ?: ""
            ) ?: ArtBookModel(
                id = category.id,
                artBookName = category.artBookName,
                artBookUrl = category.artBookUrl ?: "",
                priority = category.priority,
                isPro = category.isPro ?: false,
                urlSound = category.customFieldsEntity?.sound ?: ""
            )
        )
    }

    MMKVCache.setListArtBook(listSync)

    return listSync
}

fun DrawnRepositoryImpl.syncDataWithIdCategory(
    listData: MutableList<DrawnModel>,
    listResponse: MutableList<DrawnRemoteEntity>,
    idCategory: String
): List<DrawnModel> {
    val listSync = mutableListOf<DrawnModel>()
    listResponse.forEach { data ->
        listSync.add(
            listData.find { it.id == data.id && it.zipData == data.customFieldsEntity?.artZip }
                ?.copy(
                    artBookId = data.artBookId,
                    priority = data.priority,
                    name = data.name,
                    isPro = data.isPro ?: false,
                    originalDrawn = data.originalDrawn,
                    bordersDrawn = data.customFieldsEntity?.artBorders ?: "",
                    targetDrawn = data.customFieldsEntity?.artTarget ?: "",
                ) ?: DrawnModel(
                id = data.id,
                artBookId = data.artBookId,
                priority = data.priority,
                name = data.name,
                isPro = data.isPro ?: false,
                originalDrawn = data.originalDrawn,
                bordersDrawn = data.customFieldsEntity?.artBorders ?: "",
                targetDrawn = data.customFieldsEntity?.artTarget ?: "",
                zipData = data.customFieldsEntity?.artZip ?: "",
                drawnPaths = mutableListOf<String>()
            )
        )
    }

    MMKVCache.setListDrawn(listSync, idCategory, true)

    return listSync
}