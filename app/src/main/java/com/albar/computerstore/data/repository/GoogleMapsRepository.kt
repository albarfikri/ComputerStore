package com.albar.computerstore.data.repository

import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.interfaceretrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GoogleMapsRepository {
    fun getDirection(url: String): Flow<Result<Any>> = flow<Result<Any>> {
        emit(Result.Loading)

        val response = RetrofitClient.retrofitApi.getDirection(url)

        if (response.body()?.directionRouteModels?.size!! > 0) {
            emit(Result.Success(response.body()!!))
        } else {
            emit(Result.Error(response.body()?.error!!))
        }
    }.flowOn(Dispatchers.IO)
        .catch {
            if (it.message.isNullOrEmpty()) {
                emit(Result.Error("No route found"))
            } else {
                emit(Result.Error(it.message.toString()))
            }
        }
}