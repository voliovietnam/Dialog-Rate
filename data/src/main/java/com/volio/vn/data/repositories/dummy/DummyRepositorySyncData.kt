package com.volio.vn.data.repositories.dummy

import com.volio.vn.data.entities.remote.dummy.DummyCategoryRemoteEntity
import com.volio.vn.data.entities.remote.dummy.DummyRemoteEntity
import com.volio.vn.data.mmkv.MMKVCache
import com.volio.vn.data.models.DummyCategoryModel
import com.volio.vn.data.models.DummyModel

fun DummyRepositoryImpl.syncDataWithIdCategory(
    listData: MutableList<DummyModel>,
    listResponse: MutableList<DummyRemoteEntity>,
    idCategory: String
): List<DummyModel> {
    val listSync = mutableListOf<DummyModel>()
    listResponse.forEach { frame ->
        listSync.add(
            listData.find { it.id == frame.id }?.copy(
                id = frame.id,
                idCategory = frame.idCategory,
                remotePath = frame.customFieldsEntity?.remote_path ?: "",
                priority = frame.priority,
            ) ?: DummyModel(
                id = frame.id,
                idCategory = frame.idCategory,
                remotePath = frame.customFieldsEntity?.remote_path ?: "",
                priority = frame.priority,
                localPath = ""
            )
        )
    }

    MMKVCache.setListDummyModel(listSync, idCategory, true)

    return listSync
}

fun DummyRepositoryImpl.syncDataCategory(
    listResponse: MutableList<DummyCategoryRemoteEntity>,
    listData: MutableList<DummyCategoryModel>
): List<DummyCategoryModel> {
    val listSync = mutableListOf<DummyCategoryModel>()
    listResponse.forEach { category ->
        listSync.add(
            listData.find { it.id == category.id }?.copy(
                id = category.id,
                name = category.name,
                priority = category.priority
            ) ?: DummyCategoryModel(
                id = category.id,
                name = category.name,
                priority = category.priority
            )
        )
    }

    MMKVCache.setListDummyCategory(listSync)

    return listSync
}