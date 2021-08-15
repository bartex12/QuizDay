package com.bartex.quizday.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build

//вариант с устаревшими методами
fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
    return netInfo != null && netInfo.isConnected && netInfo.isAvailable()
}

//вариант 1 - с разделением по SDK через ||
fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT < 23) {
        val netInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        netInfo?. let{
            return netInfo.isConnected && (netInfo.type == ConnectivityManager.TYPE_WIFI
                    || netInfo.type == ConnectivityManager.TYPE_MOBILE)
        }
    } else {
        val netInfo = connectivityManager.activeNetwork
        netInfo?. let {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(netInfo)
            if (networkCapabilities != null) {
                return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            }
        }
    }
    return false
}

//вариант 2 - с разделением по SDK, через when и ?: return false
fun isInternetAvailable(context: Context): Boolean {
    var result = false
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val netInfo: Network = cm.activeNetwork ?: return false
        val networkCapabilities = cm.getNetworkCapabilities(netInfo) ?: return false
        result = when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        cm.run {
            cm.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
    }
    return result
}