package com.starcraft.madtaxi

import android.app.Application


class MadTaxiApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        sInstance = this
    }


    companion object {
        private lateinit var sInstance: MadTaxiApplication

        fun getInstance(): MadTaxiApplication {
            return MadTaxiApplication.sInstance
        }
    }

}