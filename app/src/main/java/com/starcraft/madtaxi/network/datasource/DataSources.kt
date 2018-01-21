package com.starcraft.madtaxi.network.datasource

import android.support.annotation.VisibleForTesting
import com.starcraft.madtaxi.network.RestManager
import java.lang.reflect.InvocationTargetException

object DataSources {
    private val mDataSources = HashMap<Any, BaseDataSource<*, *>>()
    private var REST_MANAGER = RestManager.getInstance()


    fun <T : BaseDataSource<*, *>> getDataSource(dataSourceClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        var response: T? = mDataSources[dataSourceClass] as T?
        if (response == null) {

            try {
                response = dataSourceClass.getDeclaredConstructor().newInstance()
                mDataSources.put(dataSourceClass, response as BaseDataSource<*, *>)
            } catch (e: InstantiationException) {
                // Empty
            } catch (e: IllegalAccessException) {
                // Empty
            } catch (e: InvocationTargetException) {
                // Empty
            } catch (e: NoSuchMethodException) {
                // Empty
            }

        }
        response!!.setRestManager(REST_MANAGER)
        return response
    }

    fun clearDataSources() {
        for (baseDataSource in mDataSources.values) {
            baseDataSource.clear()
        }
    }

    fun getDataSourceRestManager(): RestManager {
        return REST_MANAGER
    }

    @VisibleForTesting
    fun <T : BaseDataSource<*, *>> setDataSource(dataSourceClass: Class<T>,
                                                 dataSource: T) {
        // Intended to put mocks of specific data sources. Should disappear as soon as all datasources are injected
        // because we will be able to just replace the datasources module by a test datasources module.
        mDataSources.put(dataSourceClass, dataSource)
    }

    @VisibleForTesting
    fun setRestManager(restManager: RestManager) {
        REST_MANAGER = restManager
    }

    fun restart() {
        REST_MANAGER.restart()
    }
}