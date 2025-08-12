package com.emreonal.eterationcase.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.emreonal.eterationcase.data.local.dao.CartDao
import com.emreonal.eterationcase.data.local.dao.FavoriteDao
import com.emreonal.eterationcase.data.local.entity.CartEntity
import com.emreonal.eterationcase.data.local.entity.FavoriteEntity

@Database(
    entities = [CartEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun favoriteDao(): FavoriteDao
}