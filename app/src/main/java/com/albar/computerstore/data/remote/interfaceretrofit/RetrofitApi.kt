package com.albar.computerstore.data.remote.interfaceretrofit

import com.albar.computerstore.data.remote.entity.DirectionResponseModel
import com.albar.computerstore.data.remote.entity.GoogleResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface RetrofitApi {
    @GET
    suspend fun getNearByPlaces(@Url url: String): Response<GoogleResponseModel>

    @GET
    suspend fun getDirection(@Url url: String): Response<DirectionResponseModel>
}