package com.starcraft.madtaxi.network.datastore

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.starcraft.madtaxi.MadTaxiApplication


class SharedPreferencesController private constructor() {

    private val SHARED_PREFERENCES_FILE = "mad_taxi_shared_prefences"

    // SHARED PREFERENCES KEYS
    private val AUTH_TOKEN_KEY = "AUTH_TOKEN"

    private var mSharedPreferences: SharedPreferences

    fun getAuthToken(): String? {
        return mSharedPreferences.getString(AUTH_TOKEN_KEY, null)
    }

    @Synchronized
    fun setAuthToken(authToken: String?) {
        getEditor().putString(AUTH_TOKEN_KEY, authToken).apply()
    }

    private fun getEditor(): Editor {
        return mSharedPreferences.edit()
    }

    companion object Singleton {
        private val INSTANCE = SharedPreferencesController()

        fun getInstance(): SharedPreferencesController {
            return INSTANCE
        }
    }

    init {
        mSharedPreferences = MadTaxiApplication.getInstance().getSharedPreferences(
                SHARED_PREFERENCES_FILE,
                Context.MODE_PRIVATE)
    }
}