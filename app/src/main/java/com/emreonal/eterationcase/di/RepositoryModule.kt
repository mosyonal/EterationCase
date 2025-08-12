package com.emreonal.eterationcase.di

import com.emreonal.eterationcase.data.repository.CartRepositoryImpl
import com.emreonal.eterationcase.data.repository.FavoriteRepositoryImpl
import com.emreonal.eterationcase.data.repository.ProductRepositoryImpl
import com.emreonal.eterationcase.domain.repository.CartRepository
import com.emreonal.eterationcase.domain.repository.FavoriteRepository
import com.emreonal.eterationcase.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindProductRepo(impl: ProductRepositoryImpl): ProductRepository
    @Binds @Singleton abstract fun bindCartRepo(impl: CartRepositoryImpl): CartRepository
    @Binds @Singleton abstract fun bindFavoriteRepo(impl: FavoriteRepositoryImpl): FavoriteRepository
}
