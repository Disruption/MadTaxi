package com.starcraft.madtaxi.network

import android.accounts.NetworkErrorException
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.starcraft.madtaxi.network.BaseRequest.Method
import com.google.gson.Gson
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.OkUrlFactory
import org.apache.commons.io.IOUtils
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URI
import java.util.*
import java.util.Collections.synchronizedMap
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class RestManager private constructor() {
    private val CONTENT_TYPE = "Content-type"
    private val ENCODING = "UTF-8"
    private val READ_TIMEOUT: Long = 60
    private val CONNECTION_TIMEOUT: Long = 15
    private val mOkHttpClient = OkHttpClient()

    private val mPendingRequests = synchronizedMap(
            HashMap<BaseRequest<Any>, BaseRequestListener<Any>>())

    private var mThreadPoolExecutor: ThreadPoolExecutor? = null

    init {
        mOkHttpClient.setConnectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        mOkHttpClient.setReadTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        start()
    }

    private fun start() {
        mThreadPoolExecutor = Executors.newFixedThreadPool(10) as ThreadPoolExecutor
        if (!mPendingRequests.isEmpty()) {
            executePendingRequests()
        }
    }

    private fun executePendingRequests() {
        synchronized(mPendingRequests) {
            for ((key, value) in mPendingRequests) {
                executeRequest(key, value)
            }
            mPendingRequests.clear()
        }
    }

    fun <RESPONSE> executeRequest(request: BaseRequest<RESPONSE>,
                                  requestListener: BaseRequestListener<RESPONSE>?) {
        if (!isStarted()) {
            @Suppress("UNCHECKED_CAST")
            mPendingRequests.put(request as BaseRequest<Any>, requestListener as BaseRequestListener<Any>)
            return
        }
        Log.d("RestManager", "Added request " + request.javaClass)
        Log.d("RestManager",
                "Current pending requests: " + mThreadPoolExecutor!!.queue.size + " with " + mThreadPoolExecutor!!.getActiveCount() + " active threads")
        mThreadPoolExecutor!!.execute(Runnable {
            var response: RESPONSE? = null
            var exception: RestException? = null
            try {
                response = requestDataToNetwork(request)
            } catch (e: RestException) {
                exception = e
                Log.e("RESTMANAGER",
                        if (e.cause != null && e.cause.message != null) e.cause.message else "GENERIC ERROR")
            }

            if (requestListener == null) {
                return@Runnable
            }

            // Run callback calls on main thread as they affect views.
            val handler = Handler(Looper.getMainLooper())
            handler.post({
                if (exception != null) {
                    requestListener.onRequestFailed(exception!!)
                } else {
                    requestListener.onRequestSuccessful(response)
                }
            })
        })
    }

    private fun isStarted(): Boolean {
        return mThreadPoolExecutor != null && !mThreadPoolExecutor!!.isShutdown
    }

    @Throws(RestException::class)
    private fun <RESPONSE> requestDataToNetwork(request: BaseRequest<RESPONSE>): RESPONSE {
        try {
            val uriBuilder = Uri.parse(request.getUrl()).buildUpon()
            CookieHandler.setDefault(CookieManager(null, CookiePolicy.ACCEPT_ALL))
            val uri: URI
            uri = URI(uriBuilder.build().toString())

            val connection = getConnection(request, uri)

            connection.requestMethod = request.getMethod().toString()
            if (request.getBody() != null) {
                request.writeBody(connection.outputStream)
            }
            val responseCode = connection.responseCode
            if (responseCode >= 400) {
                try {
                    val errorBody = IOUtils.toString(connection.errorStream, ENCODING)
                    // TODO is this provided? If it is, build message error here
                } catch (e: Exception) {
                    // Ignore
                }

                throw RestException(NetworkErrorException("Request failed with code " + responseCode))

            }

            val body = IOUtils.toString(connection.inputStream, ENCODING)
            val response = request.processContent(body, connection.headerFields)

            connection.inputStream?.close()

            connection.disconnect()

            Log.d("RestManager",
                    "Current pending requests: " + mThreadPoolExecutor!!.queue.size + " with " + mThreadPoolExecutor!!.getActiveCount() + " active threads")
            return response
        } catch (re: RestException) {
            throw re
        } catch (exception: Exception) {
            throw RestException(exception)
        }

    }

    @Throws(MalformedURLException::class)
    private fun <R> getConnection(request: BaseRequest<R>,
                                  uri: URI): HttpURLConnection {
        val urlFactory = OkUrlFactory(mOkHttpClient)
        val conn = urlFactory.open(uri.toURL())
        val headers = request.getHeaders()
        for (header in headers.keys) {
            for (s in headers[header]!!) {
                conn.addRequestProperty(header, s)
            }
        }

        if (request.getMethod() === BaseRequest.Method.POST || request.getMethod() === Method.PUT) {
            // For POST, if the request doesn't explicitly set a content type, we set it to application/json. Some
            // web services might not work without it
            if (!headers.containsKey(CONTENT_TYPE)) {
                conn.addRequestProperty(CONTENT_TYPE, "application/json")
            }
        }

        return conn
    }

    companion object {
        private val INSTANCE = RestManager()

        fun getInstance(): RestManager {
            return INSTANCE
        }
    }


    fun restart() {
        stop()
        start()
    }

    private fun stop() {
        if (isStarted()) {
            mThreadPoolExecutor!!.shutdown()
        }
        mThreadPoolExecutor = null
    }

}

