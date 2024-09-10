package com.volio.vn.data.repositories.dummy

import android.app.Application
import com.volio.vn.data.entities.DataState
import com.volio.vn.data.mmkv.MMKVCache
import com.volio.vn.data.models.DummyCategoryModel
import com.volio.vn.data.models.DummyModel
import com.volio.vn.data.repositories.UnzipUtil
import com.volio.vn.data.service.api.SampleApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class DummyRepositoryImpl @Inject constructor(
    private val appContext: Application,
    private val apiRemote: SampleApi,
) : DummyRepository {
    override suspend fun getAllDummyCategory(): Flow<DataState<List<DummyCategoryModel>>> =
        flow {
            emit(DataState.Loading())
            val localData = MMKVCache.getAlDummyCategory().sortedBy { it.priority }

            if (localData.isNotEmpty()) {
                emit(DataState.Success(localData))
            }

            kotlin.runCatching {
                val listResponse =
                    apiRemote.getAllDummyCategory("08676feb-26e0-4489-8e96-3567aea8355c").data

                val listSync = syncDataCategory(
                    listResponse.toMutableList(),
                    localData.toMutableList()
                ).sortedBy { it.priority }

                if (listSync != localData) emit(DataState.Success(listSync))
            }
        }.flowOn(Dispatchers.IO)
            .catch { e ->
                emit(
                    DataState.Error(Exception(e.message))
                )
            }

    override suspend fun getDummyByCategoryId(
        categoryId: String
    ): Flow<DataState<List<DummyModel>>> =
        flow {
            emit(DataState.Loading())
            val localData = MMKVCache.getAllDummyModel()

            val dataCategory = MMKVCache.getAllDummyModel().filter { it.idCategory == categoryId }
                .sortedBy { it.priority }

            if (dataCategory.isNotEmpty()) {
                emit(DataState.Success(dataCategory))
            }

            kotlin.runCatching {
                val listData = apiRemote.getDummyByIdDummyCategory(categoryId, 0, 100).data
                val listSync =
                    syncDataWithIdCategory(
                        localData.toMutableList(),
                        listData.toMutableList(),
                        categoryId
                    ).sortedBy { it.priority }

                if (dataCategory != listSync) emit(DataState.Success(listSync))
            }
        }.flowOn(Dispatchers.IO).catch { e ->
            emit(
                DataState.Error(Exception(e.message))
            )
        }


    override suspend fun downloadDummyZip(dummyModel: DummyModel): Flow<DataState<DummyModel>> =
        flow {
            emit(DataState.Loading())

            val zipUrl = dummyModel.remotePath
            if (zipUrl.isNotEmpty()) {
                val dirToSave = getDirUnzip(zipUrl)
                val fileName = zipUrl.substringAfterLast("/")
                if (fileName.isNotBlank()) {
                    val fileZip = File(dirToSave, fileName)
                    fileZip.parentFile?.mkdirs()
                    fileZip.createNewFile()

                    var newData = dummyModel.copy()
                    withContext(Dispatchers.IO) {
                        val promise = async {
                            downloadFile(
                                zipUrl,
                                fileZip.absolutePath
                            ) { isSuccess, path ->
                                if (isSuccess) {
                                    newData = dummyModel.copy(localPath = path)
                                }
                            }
                        }
                        promise.await()
                    }

                    if (newData.localPath != "") {
                        MMKVCache.setListDummyModel(listOf(newData), newData.idCategory, false)
                        emit(DataState.Success(newData))
                    } else {
                        emit(
                            DataState.Error(
                                Exception("Not unzip")
                            )
                        )
                    }
                }
            } else {
                emit(
                    DataState.Error(
                        Exception("Not unzip")
                    )
                )
            }
        }.flowOn(Dispatchers.IO).catch { e ->

            emit(
                DataState.Error(
                    Exception(e.message)
                )
            )
        }

    private fun getDirUnzip(zipUrl: String): File {
        val zipFileName =
            zipUrl.substringAfterLast("/").substringBeforeLast(".zip").substringBeforeLast("_")
        val dir = File(appContext.filesDir, "/draw/template/" + zipFileName)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private suspend fun downloadFile(
        url: String, fileName: String, onDone: (Boolean, String) -> Unit
    ) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url)
                .build()
            val response = client.newCall(request).execute()
            response.body?.byteStream().use { inp ->
                BufferedInputStream(inp).use { bis ->
                    FileOutputStream(fileName).use { fos ->
                        val data = ByteArray(1024)
                        var count: Int
                        while (bis.read(data, 0, 1024).also { count = it } != -1) {
                            fos.write(data, 0, count)
                        }
                    }
                }
                try {
                    val zip = File(fileName)
                    val destinationFolder = zip.parentFile
                    val pathFile = UnzipUtil.rajDhaniSuperFastUnzip(
                        zip.absolutePath, destinationFolder.absolutePath
                    )

                    if (pathFile != null) {
                        if (File(pathFile).exists()) {
                            zip.delete()
                            withContext(Dispatchers.Main) {
                                onDone(true, pathFile)
                            }
                        } else {
                            destinationFolder.deleteOnExit()
                            withContext(Dispatchers.Main) {
                                onDone(false, "")
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            onDone(false, "")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        onDone(false, "")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}