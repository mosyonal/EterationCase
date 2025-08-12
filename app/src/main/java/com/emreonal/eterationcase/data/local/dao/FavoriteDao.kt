package com.emreonal.eterationcase.data.local.dao

import androidx.room.*
import com.emreonal.eterationcase.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun observe(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(fav: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE productId = :id")
    suspend fun remove(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE productId = :id)")
    suspend fun isFavorite(id: String): Boolean
}