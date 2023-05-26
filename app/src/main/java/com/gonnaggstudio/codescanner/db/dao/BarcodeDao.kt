package com.gonnaggstudio.codescanner.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.gonnaggstudio.codescanner.db.Constants.BARCODE_TABLE
import com.gonnaggstudio.codescanner.db.entity.BarcodeEntity

@Dao
interface BarcodeDao {

    @Insert
    suspend fun insert(barcodeEntity: BarcodeEntity)

    @Query("SELECT * FROM $BARCODE_TABLE ORDER BY scanned_at DESC")
    fun getAllBarcodesDescPaging(): PagingSource<Int, BarcodeEntity>

    @Query("SELECT * FROM $BARCODE_TABLE WHERE url = :url")
    suspend fun getBarcodeByUrl(url: String): List<BarcodeEntity>

    @Query("SELECT * FROM $BARCODE_TABLE WHERE id = :id")
    suspend fun getBarcodeById(id: Int): BarcodeEntity?

    @Query("SELECT * FROM $BARCODE_TABLE WHERE url LIKE :inputString")
    suspend fun getBarcodeByUrlKeyword(inputString: String): List<BarcodeEntity>

    @Update
    suspend fun update(barcodeEntity: BarcodeEntity)

    @Delete
    suspend fun delete(barcodeEntity: BarcodeEntity)

    @Query("DELETE FROM $BARCODE_TABLE WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)

    @Query("DELETE FROM $BARCODE_TABLE")
    suspend fun deleteAll()
}
