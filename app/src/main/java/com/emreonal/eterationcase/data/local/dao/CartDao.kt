package com.emreonal.eterationcase.data.local.dao

import androidx.room.*
import com.emreonal.eterationcase.data.local.entity.CartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun observeCart(): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CartEntity)

    @Query("UPDATE cart_items SET quantity = quantity + 1 WHERE productId = :productId")
    suspend fun inc(productId: String): Int

    @Query("UPDATE cart_items SET quantity = quantity - 1 WHERE productId = :id AND quantity > 1")
    suspend fun dec(id: String)

    @Query("DELETE FROM cart_items WHERE productId = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM cart_items")
    suspend fun clear()
}
