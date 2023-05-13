package com.gonnaggstudio.codescanner.di

import android.content.Context
import androidx.room.Room
import com.gonnaggstudio.codescanner.db.AppDatabase
import com.gonnaggstudio.codescanner.db.Constants.BARCODE_DATABASE
import com.gonnaggstudio.codescanner.db.dao.BarcodeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provide(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, AppDatabase::class.java, BARCODE_DATABASE
    )
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun provideBarcodeDao(appDatabase: AppDatabase): BarcodeDao {
        return appDatabase.barcodeDao()
    }
}
