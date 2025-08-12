package com.emreonal.eterationcase.data.remote

import com.emreonal.eterationcase.data.remote.dto.ProductDto
import retrofit2.http.GET

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<ProductDto>
}