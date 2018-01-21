package com.starcraft.madtaxi.network.datasource

import com.starcraft.madtaxi.network.BaseRequest


abstract class UniqueValueDataSource<RESPONSE_TYPE> : BaseDataSource<Void, RESPONSE_TYPE>() {

    private var mCachedValue: RESPONSE_TYPE? = null
    private var mCacheTime = 0L

    override fun getCachedValue(key: Void?): RESPONSE_TYPE? {
        return if (isCacheExpired) null else mCachedValue
    }

    override fun hasCachedValueForKey(key: Void?): Boolean {
        return !isCacheExpired && mCachedValue != null
    }

    override fun getDefaultRequest(query: Void): BaseRequest<RESPONSE_TYPE> {
        return getDefaultRequest()
    }

    override fun cacheValue(query_type: Void?, response: RESPONSE_TYPE?) {
        mCachedValue = response
        mCacheTime = System.currentTimeMillis()
    }

    override fun clear() {
        mCacheTime = 0L
        mCachedValue = null
    }

    private val isCacheExpired: Boolean
        get() = System.currentTimeMillis() - mCacheTime > getCacheTtlInMillis()

}