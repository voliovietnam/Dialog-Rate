package com.volio.vn.data.repositories.drawn

import android.app.Application
import android.util.Log
import com.volio.vn.data.entities.DataState
import com.volio.vn.data.mmkv.MMKVCache
import com.volio.vn.data.models.art_book.ArtBookModel
import com.volio.vn.data.models.art_book.DrawnModel
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
import java.util.Collections
import javax.inject.Inject

class DrawnRepositoryImpl @Inject constructor(
    private val appContext: Application,
    private val apiRemote: SampleApi,
) : DrawnRepository {

    override suspend fun getArtBook(
        categoryId: String, isSynAll: Boolean
    ): Flow<DataState<List<ArtBookModel>>> =
        flow {
            emit(DataState.Loading())
            val localData = MMKVCache.getAllArtBook().sortedBy { it.priority }

            if (localData.isNotEmpty() && !isSynAll) {
                emit(DataState.Success(localData))
            }

            val listResponse = apiRemote.getAllArtBook(categoryId).data

            val listSync = syncDataArtBook(
                listResponse.toMutableList(), localData.toMutableList()
            ).sortedBy { it.priority }

            if ((listSync != localData && !isSynAll) || isSynAll) {
                emit(DataState.Success(listSync))
            }
        }.flowOn(Dispatchers.IO).catch { e ->
            emit(
                DataState.Error(Exception(e.message))
            )
        }

    override suspend fun getDrawnByIdArtBook(
        categoryId: String, isSynAll: Boolean
    ): Flow<DataState<List<DrawnModel>>> =
        flow {
            emit(DataState.Loading())

            val localData = MMKVCache.getAllDrawn()

            val dataCategory = MMKVCache.getAllDrawn().filter { it.artBookId == categoryId }
                .sortedBy { it.priority }

            if (dataCategory.isNotEmpty() && !isSynAll) {
                emit(DataState.Success(dataCategory))
            }

            val listData = apiRemote.getDrawnByIdArtBook(categoryId, 0, 100).data
            val listSync = syncDataWithIdCategory(
                localData.toMutableList(), listData.toMutableList(), categoryId
            ).sortedBy { it.priority }

            if ((listSync != localData && !isSynAll) || isSynAll) {
                emit(DataState.Success(listSync))
            }

        }.flowOn(Dispatchers.IO).catch { e ->
            emit(
                DataState.Error(Exception(e.message))
            )
        }

    override suspend fun getAllDataDrawn(
        categoryId: String
    ): Flow<DataState<List<Pair<ArtBookModel, List<DrawnModel>>>>> =
        flow {
            emit(DataState.Loading())
            val dataLocal = mutableListOf<Pair<ArtBookModel, List<DrawnModel>>>()
            val artBookLocal = MMKVCache.getAllArtBook().sortedBy { it.priority }

            if (artBookLocal.isNotEmpty()) {
                artBookLocal.forEach {
                    val artBook = it
                    val listDrawnInArtBook =
                        MMKVCache.getAllDrawn().filter { it.artBookId == artBook.id }
                            .sortedBy { it.priority }

                    if (listDrawnInArtBook.isNotEmpty()) {
                        dataLocal.add(
                            Pair(
                                artBook,
                                listDrawnInArtBook
                            )
                        )
                    }
                }
                emit(DataState.Success(dataLocal))
            }

            val dataFromSever = mutableListOf<Pair<ArtBookModel, List<DrawnModel>>>()
            getArtBook(categoryId, true).collect {
                if (it is DataState.Success) {
                    it.data.let { categogry ->
                        categogry.forEach { artBook ->
                            getDrawnByIdArtBook(artBook.id, true).collect {
                                if (it is DataState.Success) {
                                    if (it.data.isNotEmpty()) {
                                        dataFromSever.add(Pair(artBook, it.data))
                                    }
                                }
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


    override suspend fun downLoadDrawn(drawnModel: DrawnModel): Flow<DataState<DrawnModel>> = flow {
        emit(DataState.Loading())

        val zipUrl = drawnModel.zipData
        if (zipUrl.isNotEmpty()) {
            val dirToSave = getDirUnzip(zipUrl)
            val fileName = zipUrl.substringAfterLast("/")
            if (fileName.isNotBlank()) {
                val fileZip = File(dirToSave, fileName)
                fileZip.parentFile?.mkdirs()
                fileZip.createNewFile()

                var newData = drawnModel.copy()
                withContext(Dispatchers.IO) {
                    val promise = async {
                        downloadFile(
                            zipUrl, fileZip.absolutePath
                        ) { isSuccess, path ->
                            if (isSuccess) {
                                val files: Array<File> = File(path).listFiles()

                                Collections.sort<File>(files.asList(), object : Comparator<File> {
                                    private val NATURAL_SORT: Comparator<String> =
                                        WindowsExplorerComparator()

                                    override fun compare(o1: File, o2: File): Int {
                                        return NATURAL_SORT.compare(o1.name, o2.name)
                                    }
                                })

                                val listArt = mutableListOf<String>()

                                files.forEach { file ->
                                    if (file.path.substringAfterLast(".") == "png") {
                                        listArt.add(file.path)
                                    }
                                }

                                newData = drawnModel.copy(
                                    drawnPaths = listArt
                                )
                            }
                        }
                    }
                    promise.await()
                }

                if (newData.drawnPaths.isNotEmpty()) {
                    MMKVCache.setListDrawn(listOf(newData), newData.artBookId, false)
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
            val request = Request.Builder().url(url).build()
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