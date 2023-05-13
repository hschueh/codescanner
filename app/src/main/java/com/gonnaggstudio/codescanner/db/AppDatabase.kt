package com.gonnaggstudio.codescanner.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gonnaggstudio.codescanner.db.dao.BarcodeDao
import com.gonnaggstudio.codescanner.db.entity.BarcodeEntity

@Database(entities = [BarcodeEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun barcodeDao(): BarcodeDao
}
