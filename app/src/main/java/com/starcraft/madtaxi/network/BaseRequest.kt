package com.starcraft.madtaxi.network

import com.starcraft.madtaxi.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.apache.commons.lang3.StringUtils
import java.io.IOException
import java.io.OutputStream
import java.util.*

abstract class BaseRequest<RESPONSE> {

    protected val gson = initGson()
    private val requestParams = HashMap<String, Any>()

    private fun initGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }

    fun getUrl(): String {
        var url = getEndpointUrl()
        if (!requestParams.isEmpty()) {
            url = appendRequestParamsToUrl(url)
        }
        return url
    }

    abstract fun getEndpointUrl(): String

    private fun appendRequestParamsToUrl(url: String): String {
        var isFirst = true
        val urlWithParams = StringBuilder(url)
        urlWithParams.append("?")

        for (entry in requestParams.entries) {
            if (isFirst) {
                isFirst = false
            } else {
                urlWithParams.append("&")
            }
            urlWithParams.append(entry.key).append("=").append(entry.value)
        }

        return urlWithParams.toString()
    }

    fun processContent(responseBody: String,
                       headerFields: Map<String, List<String>>): RESPONSE {
        return updateResponseWithHeaderInfo(gson.fromJson(responseBody, getResponseClass()),
                headerFields)
    }

    protected fun updateResponseWithHeaderInfo(response: RESPONSE,
                                               headerFields: Map<String, List<String>>): RESPONSE {
        return response
    }

    protected abstract fun getResponseClass(): Class<RESPONSE>

    protected fun getDomain(): String {
        return Constants.BASE_URL
    }

    protected fun addRequestParam(paramKey: String, paramValue: Any) {
        requestParams[paramKey] = paramValue
    }

    protected fun removeRequestParam(paramKey: Any) {
        requestParams.remove(paramKey)
    }

    protected fun getRequestParam(paramKey: String): Any? {
        return requestParams[paramKey]
    }

    open fun getHeaders(): MutableMap<String, List<String>> {
        val headers = HashMap<String, List<String>>()

        if (getMethod() == Method.POST || getMethod() == Method.PUT) {
            headers["Content-Type"] = Collections.singletonList("application/json")
        }

        headers[ACCEPTS_HEADER_KEY] = Collections.singletonList(CONTENT_TYPE_HEADER_KEY)

        return headers
    }

    open fun getMethod(): Method {
        return if (StringUtils.isEmpty(getBody())) Method.GET else Method.POST
    }

    open fun getBody(): String? {
        return null
    }

    @Throws(IOException::class)
    fun writeBody(stream: OutputStream) {
        val body = getBody()
        if (body != null && !body.isEmpty()) {
            stream.write(body.toByteArray(charset(ENCODING)))
            stream.close()
        }
    }

    fun getCacheKey(): String? {
        return null
    }

    enum class Method {
        GET, POST, PUT
    }

    companion object {
        protected const val PAGE_PARAM_KEY = "page"
        private const val CONTENT_TYPE_HEADER_KEY = "application/json"
        private const val ACCEPTS_HEADER_KEY = "Accept"
        private const val ENCODING = "UTF-8"
    }
}
