package com.emreonal.eterationcase.di

import android.content.Context
import androidx.room.Room
import com.emreonal.eterationcase.data.local.AppDatabase
import com.emreonal.eterationcase.data.local.dao.CartDao
import com.emreonal.eterationcase.data.local.dao.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room
            .databaseBuilder(ctx, AppDatabase::class.java, "shopping.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCartDao(db: AppDatabase): CartDao = db.cartDao()
    @Provides
    fun provideFavoriteDao(db: AppDatabase): FavoriteDao = db.favoriteDao()
}
