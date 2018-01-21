package com.starcraft.madtaxi.network.datasource

import android.os.Handler
import android.os.Looper
import com.starcraft.madtaxi.MadTaxiApplication
import com.starcraft.madtaxi.R
import com.starcraft.madtaxi.network.BaseRequest
import com.starcraft.madtaxi.network.BaseRequestListener
import com.starcraft.madtaxi.network.RestException
import com.starcraft.madtaxi.network.RestManager
import java.lang.ref.WeakReference


abstract class BaseDataSource<QUERY_TYPE, RESPONSE_TYPE> {
    private val CACHE_TTL_IN_MILLIS = (5 * 60 * 1000).toLong() // Five minutes
    private var mHandler: Handler
    private var mRestManager: RestManager? = null

    fun requestData(dataListener: DataListener<RESPONSE_TYPE>) {
        requestData(dataListener, getDefaultRequest())
    }

    fun requestData(dataListener: DataListener<RESPONSE_TYPE>,
                    dataRequest: BaseRequest<RESPONSE_TYPE>) {
        requestData(dataListener, dataRequest, null)
    }

    protected abstract fun getDefaultRequest(): BaseRequest<RESPONSE_TYPE>

    fun requestData(dataListener: DataListener<RESPONSE_TYPE>?,
                    dataRequest: BaseRequest<RESPONSE_TYPE>, query: QUERY_TYPE?) {
        val cacheKey = getCacheKey(dataRequest, query)
        val cachedValue = getCachedValue(cacheKey)

        if (hasCachedValueForKey(cacheKey)) {
            if (dataListener != null) {
                // If we are already on the main thread, we just call the listener
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    dataListener.onDataUpdated(cachedValue)
                    return
                }

                // In any other case, we move the data updated call to the main thread
                mHandler.post({ dataListener.onDataUpdated(cachedValue) })
            }
        } else {
            mRestManager!!.executeRequest(dataRequest, RequestListener(cacheKey, dataListener))
        }
    }

    protected fun getCacheKey(dataRequest: BaseRequest<RESPONSE_TYPE>,
                              query: QUERY_TYPE?): QUERY_TYPE? {
        // For simple datasources, the query can directly be the cache key, but in some cases we will need better
        // constraints. i.e. research request has a big amount of params.
        return query
    }

    protected abstract fun getCachedValue(key: QUERY_TYPE?): RESPONSE_TYPE?

    protected abstract fun hasCachedValueForKey(key: QUERY_TYPE?): Boolean

    fun requestData(dataListener: DataListener<RESPONSE_TYPE>, query: QUERY_TYPE) {
        requestData(dataListener, getDefaultRequest(query), query)
    }

    protected abstract fun getDefaultRequest(query: QUERY_TYPE): BaseRequest<RESPONSE_TYPE>

    fun setRestManager(restManager: RestManager) {
        mRestManager = restManager
    }

    fun getData(): RESPONSE_TYPE? {
        return getData(null)
    }

    fun getData(query: QUERY_TYPE?): RESPONSE_TYPE? {
        return getCachedValue(query)
    }

    protected open fun getCacheTtlInMillis(): Long {
        return CACHE_TTL_IN_MILLIS
    }

    protected abstract fun cacheValue(query_type: QUERY_TYPE?, response: RESPONSE_TYPE?)

    abstract fun clear()

    interface DataListener<in RESPONSE> {

        fun onDataUpdated(response: RESPONSE?)

        fun onError(errorMessage: String, exception: RestException)

        fun canProcessCallback(): Boolean

    }

    abstract class AlwaysAvailableDataListener<in RESPONSE_TYPE> : DataListener<RESPONSE_TYPE> {

        override fun canProcessCallback(): Boolean {
            return true
        }
    }

    private inner class RequestListener internal constructor(private val mQuery: QUERY_TYPE?, dataListener: DataListener<RESPONSE_TYPE>?) : BaseRequestListener<RESPONSE_TYPE> {
        private val mDataListener: WeakReference<DataListener<RESPONSE_TYPE>?> = WeakReference(dataListener)

        override fun onRequestSuccessful(response: RESPONSE_TYPE?) {
            if (response == null) return

            cacheValue(mQuery, response)
            val dataListener = mDataListener.get()
            if (dataListener != null && dataListener.canProcessCallback()) {
                dataListener.onDataUpdated(response)
            }
        }

        override fun onRequestFailed(exception: RestException) {
            mDataListener.get()?.onError(MadTaxiApplication.getInstance().getString(R.string.generic_crouton_error_message), exception)
        }

    }

    init {
        mRestManager = DataSources.getDataSourceRestManager()
        mHandler = Handler(Looper.getMainLooper())
    }
}