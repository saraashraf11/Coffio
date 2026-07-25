package com.coffeehub.pos.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Path

// Retrofit API interface — for optional remote sync
interface BrewPointApi {
    @GET("menu")
    suspend fun getRemoteMenu(): List<Map<String, Any>>

    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") orderId: Int): Map<String, Any>
}
