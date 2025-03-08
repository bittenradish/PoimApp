package com.example.poimapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.core.NetworkExceptions
import okhttp3.Interceptor
import okhttp3.Response

class NetworkConnectionInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isInternetAvailable(context)) {
            throw NetworkExceptions.NoConnectivityException()
        }
        return chain.proceed(chain.request())
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}