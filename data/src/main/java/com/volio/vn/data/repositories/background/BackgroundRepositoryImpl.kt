package com.volio.vn.data.repositories.background

import android.os.Environment
import android.util.Log
import com.volio.vn.data.entities.DataState
import com.volio.vn.data.mmkv.MMKVCache
import com.volio.vn.data.models.background.BackgroundCategoryModel
import com.volio.vn.data.models.background.BackgroundModel
import com.volio.vn.data.service.api.SampleApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject

class BackgroundRepositoryImpl @Inject constructor(
    private val apiRemote: SampleApi,
) : BackgroundRepository {
    override suspend fun getAllCategoryBackground(
        categoryId: String, isSynAll: Boolean
    ): Flow<DataState<List<BackgroundCategoryModel>>> =
        flow {
            emit(DataState.Loading())
            val localData = MMKVCache.getAllBackgroundCategory().sortedBy { it.priority }

            if (localData.isNotEmpty() && !isSynAll) {
                emit(DataState.Success(localData))
            }

            val listResponse = apiRemote.getAllCategoryBackground(categoryId).data
            val listSync = syncDataBackgroundCategory(
                listResponse.toMutableList()
            ).sortedBy { it.priority }

            if ((listSync != localData && !isSynAll) || isSynAll) emit(DataState.Success(listSync))
        }.flowOn(Dispatchers.IO)
            .catch { e ->
                Log.d("HIUIUIUIUIU", "getAllCategoryBackground: "+e.message)
                emit(DataState.Error(Exception(e.message)))
            }

    override suspend fun getAllDataBackground(
        categoryId: String
    ): Flow<DataState<List<Pair<BackgroundCategoryModel, List<BackgroundModel>>>>> =
        flow {
            emit(DataState.Loading())
            val dataLocal = MMKVCache.getBackgroundData()
            if (dataLocal.isNotEmpty()) {
                emit(DataState.Success(dataLocal))
            }

            val dataFromSever =
                mutableListOf<Pair<BackgroundCategoryModel, List<BackgroundModel>>>()

            getAllCategoryBackground(categoryId, true).collect {
                if (it is DataState.Success) {
                    it.data.forEach { categogry ->
                        getAllItemBackground(categogry.id, true).collect {
                            if (it is DataState.Success) {
                                dataFromSever.add(Pair(categogry, it.data))
                            }
                        }
                    }
                }
            }
            if (dataFromSever != dataLocal) {
                emit(DataState.Success(dataFromSever))
            }
        }.flowOn(Dispatchers.IO).catch { e ->
            emit(
                DataState.Error(Exception(e.message))
            )
        }

    override suspend fun getAllItemBackground(
        idCategory: String, isSynAll: Boolean
    ): Flow<DataState<List<BackgroundModel>>> = flow {
        emit(DataState.Loading())
        val localData = MMKVCache.getAllBackgroundModel().filter { it.categoryId == idCategory }
            .sortedBy { it.priority }

        if (localData.isNotEmpty() && !isSynAll) {
            emit(DataState.Success(localData))
        }

        val listResponse = apiRemote.getAllItemBackground(idCategory).data

        val listSync = syncDataBackgroundModel(
            listResponse.toMutableList(),
            idCategory,
        ).sortedBy { it.priority }

        if ((listSync != localData && !isSynAll) || isSynAll) emit(DataState.Success(listSync))
    }.flowOn(Dispatchers.IO)
        .catch { e ->
            emit(DataState.Error(Exception(e.message)))
        }

    override suspend fun downloadImage(backgroundModel: BackgroundModel): Flow<DataState<String>> =
        flow {
            emit(DataState.Loading())
            val path = downloadImageToExternal(backgroundModel.backgroundUrl, backgroundModel.name)
            path?.let {
                emit(DataState.Success(path))
            }
        }.flowOn(Dispatchers.IO)
            .catch { e ->
                emit(DataState.Error(Exception(e.message)))
            }

    private suspend fun downloadImageToExternal(
        path: String,
        nameFile: String
    ): String? {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(path).openConnection()
                val inputStream = connection.getInputStream()
                val picturesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val fileParent = File(picturesDir, "ColorPicker")
                if (!fileParent.exists()) {
                    fileParent.mkdirs()
                }
                val fileSave = File(fileParent, "${nameFile}_${System.currentTimeMillis()}.png")
                val outputStream = FileOutputStream(fileSave)
                inputStream.copyTo(outputStream)
                outputStream.flush()
                outputStream.close()
                fileSave.path
            } catch (e: Exception) {
                null
            }
        }
    }
}
