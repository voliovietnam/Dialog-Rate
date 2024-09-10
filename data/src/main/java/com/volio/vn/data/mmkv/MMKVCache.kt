package com.volio.vn.data.mmkv

import android.util.Log
import com.tencent.mmkv.MMKV
import com.volio.vn.data.models.DummyCategoryModel
import com.volio.vn.data.models.DummyModel
import com.volio.vn.data.models.art_book.ArtBookModel
import com.volio.vn.data.models.art_book.DrawnModel
import com.volio.vn.data.models.background.BackgroundCategoryModel
import com.volio.vn.data.models.background.BackgroundModel

object MMKVCache {
    private const val KEY_CACHE_DUMMY_CATEGORY = "KEY_CACHE_DUMMY_CATEGORY"
    private const val KEY_CACHE_DUMMY_MODEL = "KEY_CACHE_DUMMY_MODEL"

    private const val KEY_CACHE_ART_BOOK = "KEY_CACHE_ART_BOOK"
    private const val KEY_CACHE_DRAWN_MODEL = "KEY_CACHE_DRAWN_MODEL"
    private const val KEY_CACHE_BACKGROUND_CATEGORY = "KEY_CACHE_BACKGROUND_CATEGORY"
    private const val KEY_CACHE_BACKGROUND_MODEL = "KEY_CACHE_BACKGROUND_MODEL"
    private const val KEY_CACHE_ID_BACKGROUND_CATEGORY = "KEY_CACHE_ID_BACKGROUND_CATEGORY"

    @Synchronized
    fun setIdCategoryBackground(
        idCate: String
    ) {
        MMKV.defaultMMKV().encode(KEY_CACHE_ID_BACKGROUND_CATEGORY, idCate)
    }

    fun getIdCategoryBackground(): String? {
        return MMKV.defaultMMKV().decodeString(KEY_CACHE_ID_BACKGROUND_CATEGORY)
    }

    @Synchronized
    fun setListBackgroundCategory(
        listCategory: List<BackgroundCategoryModel>,
    ) {
        MMKV.defaultMMKV().encodeListParcelable(KEY_CACHE_BACKGROUND_CATEGORY, listCategory)
    }

    @Synchronized
    fun setListBackground(
        listCategory: List<BackgroundModel>,
        idCategory: String,
        isSyncFromSever: Boolean
    ) {
        val dataSave =
            if (isSyncFromSever) listCategory + getAllBackgroundModel().filter { it.categoryId != idCategory }
            else updateData(listCategory, getAllBackgroundModel())

        Log.d("HIIUIUIUIU", "setListBackground: "+dataSave)
        MMKV.defaultMMKV().encodeListParcelable(KEY_CACHE_BACKGROUND_MODEL, dataSave)
    }

    fun getBackgroundData(): List<Pair<BackgroundCategoryModel, List<BackgroundModel>>> {
        val backgroundCategories = getAllBackgroundCategory()
        val background = getAllBackgroundModel()
        return backgroundCategories.mapNotNull { cate ->
            val backgrounds = background
                .filter { it.categoryId == cate.id }
                .sortedBy { it.priority }
            if (backgrounds.isNotEmpty()) Pair(cate, backgrounds) else null
        }
    }

    fun getAllBackgroundCategory(): List<BackgroundCategoryModel> {
        return MMKV.defaultMMKV().decodeListParcelable(KEY_CACHE_BACKGROUND_CATEGORY, listOf())
            ?: listOf()
    }

    fun getAllBackgroundModel(): List<BackgroundModel> {
        return MMKV.defaultMMKV().decodeListParcelable(KEY_CACHE_BACKGROUND_MODEL, listOf())
            ?: listOf()
    }

    @Synchronized
    fun setListDummyCategory(listCategory: List<DummyCategoryModel>) {
        MMKV.defaultMMKV().encodeListParcelable(KEY_CACHE_DUMMY_CATEGORY, listCategory)
    }

    fun getAlDummyCategory(): List<DummyCategoryModel> {
        return MMKV.defaultMMKV().decodeListParcelable(KEY_CACHE_DUMMY_CATEGORY, listOf())
            ?: listOf()
    }

    @Synchronized
    fun setListDummyModel(
        listCategory: List<DummyModel>,
        idCategory: String,
        isSyncFromSever: Boolean
    ) {

        val dataSave =
            if (isSyncFromSever) listCategory + getAllDummyModel().filter { it.idCategory != idCategory }
            else updateData(listCategory, getAllDummyModel())

        MMKV.defaultMMKV().encodeListParcelable(
            KEY_CACHE_DUMMY_MODEL,
            dataSave
        )
    }

    fun getAllDummyModel(): List<DummyModel> {
        return MMKV.defaultMMKV().decodeListParcelable(KEY_CACHE_DUMMY_MODEL, listOf())
            ?: listOf()
    }


    @Synchronized
    fun setListArtBook(listCategory: List<ArtBookModel>) {
        MMKV.defaultMMKV().encodeListParcelable(KEY_CACHE_ART_BOOK, listCategory)
    }

    fun getAllArtBook(): List<ArtBookModel> {
        return MMKV.defaultMMKV().decodeListParcelable(KEY_CACHE_ART_BOOK, listOf())
            ?: listOf()
    }

    @Synchronized
    fun setListDrawn(
        listCategory: List<DrawnModel>,
        idCategory: String,
        isSyncFromSever: Boolean
    ) {

        val dataSave =
            if (isSyncFromSever) listCategory + getAllDrawn().filter { it.artBookId != idCategory }
            else updateData(listCategory, getAllDrawn())

        MMKV.defaultMMKV().encodeListParcelable(
            KEY_CACHE_DRAWN_MODEL,
            dataSave
        )
    }

    fun getAllDrawn(): List<DrawnModel> {
        return MMKV.defaultMMKV().decodeListParcelable(KEY_CACHE_DRAWN_MODEL, listOf())
            ?: listOf()
    }

    private fun <T> updateData(listUpdate: List<T>, dataAll: List<T>): List<T> {
        val listNew: MutableList<T> = mutableListOf()
        val listSave: MutableList<T> = mutableListOf()

        listSave.addAll(listUpdate)

        fun getItemUpdate(data: T): T {
            listUpdate.forEach {
                if ((it is DummyModel && data is DummyModel)) {
                    if (it.id == data.id) {
                        listSave.remove(it)
                        return it
                    }
                }
                if ((it is DrawnModel && data is DrawnModel)) {
                    if (it.id == data.id) {
                        listSave.remove(it)
                        return it
                    }
                }
            }
            return data
        }

        dataAll.forEach {
            listNew.add(getItemUpdate(it))
        }

        return listNew + listSave
    }

}