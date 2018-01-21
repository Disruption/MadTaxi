package com.starcraft.madtaxi.network.datasource

import java.util.AbstractMap.SimpleEntry

abstract class MultipleValueDataSource<QUERY_TYPE, RESPONSE_TYPE> : BaseDataSource<QUERY_TYPE, RESPONSE_TYPE>() {

    private val cache = HashMap<QUERY_TYPE, SimpleEntry<Long, RESPONSE_TYPE>>()

    override fun getCachedValue(key: QUERY_TYPE?): RESPONSE_TYPE? {
        return if (hasCachedValueForKey(key)) cache[key]!!.value else null
    }

    public override fun hasCachedValueForKey(key: QUERY_TYPE?): Boolean {
        val cachedValue = cache[key]
        return cachedValue != null && System.currentTimeMillis() - cachedValue.key < getCacheTtlInMillis()
    }

    override fun cacheValue(query_type: QUERY_TYPE?, response: RESPONSE_TYPE?) {
        if (query_type != null && response != null) {
            cache.put(query_type, SimpleEntry(System.currentTimeMillis(), response))
        }
    }

    override fun clear() {
        cache.clear()
    }
}