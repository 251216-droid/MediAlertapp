package com.fernanda.medialert.data.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class NetworkDebugInterceptor : Interceptor {
    companion object {
        private const val TAG = "NetworkDebug"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val sanitizedUrl = request.url.newBuilder().query(null).build()
        val startNs = System.nanoTime()

        return try {
            val response = chain.proceed(request)
            val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
            Log.i(
                TAG,
                "${request.method} $sanitizedUrl -> ${response.code} (${tookMs} ms)"
            )
            response
        } catch (e: IOException) {
            val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
            Log.e(
                TAG,
                "${request.method} $sanitizedUrl -> IO error (${tookMs} ms): ${e.message}",
                e
            )
            throw e
        } catch (e: Exception) {
            val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
            Log.e(
                TAG,
                "${request.method} $sanitizedUrl -> Unexpected error (${tookMs} ms): ${e.message}",
                e
            )
            throw e
        }
    }
}
