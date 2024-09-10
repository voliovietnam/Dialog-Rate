package com.volio.vn.data.repositories.background

import android.util.Log
import com.volio.vn.data.entities.remote.background.BackgroundRemoteEntity
import com.volio.vn.data.entities.remote.drawn.DrawnRemoteEntity
import com.volio.vn.data.mmkv.MMKVCache
import com.volio.vn.data.models.background.BackgroundCategoryModel
import com.volio.vn.data.models.background.BackgroundModel

fun BackgroundRepositoryImpl.syncDataBackgroundCategory(
    listResponse: MutableList<BackgroundRemoteEntity>,
): List<BackgroundCategoryModel> {
    val listSync = listResponse.map { category ->
        BackgroundCategoryModel(
            id = category.id,
            backgroundName = category.name ?: "",
            backgroundCategoryUrl = category.photo ?: "",
            priority = category.priority,
            isPro = category.isPro,
            sound = category.backgroundCustomFieldsEntity?.sound ?: ""
        )
    }
    MMKVCache.setListBackgroundCategory(listSync)
    return listSync
}

fun BackgroundRepositoryImpl.syncDataBackgroundModel(
    listResponse: MutableList<BackgroundRemoteEntity>,
    idCategory: String
): List<BackgroundModel> {
    val listSync = listResponse.map { data ->
        BackgroundModel(
            id = data.id,
            categoryId = idCategory,
            name = data.name,
            backgroundUrl = data.photo ?: "",
            isPro = data.isPro,
            priority = data.priority,
            sound = data.backgroundCustomFieldsEntity?.sound ?: ""
        )
    }
    MMKVCache.setListBackground(listSync, idCategory, true)
    return listSync
}