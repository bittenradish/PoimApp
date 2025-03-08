package com.example.poimapp.utils

import com.example.core.NetworkExceptions
import okhttp3.Interceptor
import okhttp3.Response

class NetworkErrorMappingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        return when (response.code) {
            in 400..499 -> throw NetworkExceptions.ClientErrorException(code = response.code)
            in 500..599 -> throw NetworkExceptions.ServerErrorException(code = response.code)
            else -> response
        }
    }
}