package com.starcraft.madtaxi

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.starcraft.madtaxi.network.RestException
import com.starcraft.madtaxi.network.datasource.BaseDataSource
import com.starcraft.madtaxi.network.datasource.DataSources
import com.starcraft.madtaxi.network.datasource.fare.FareDataSource
import com.starcraft.madtaxi.network.fare.response.FareResponse

class SplashActivity : AppCompatActivity(), BaseDataSource.DataListener<FareResponse> {
    override fun onDataUpdated(response: FareResponse?) {

    }

    override fun onError(errorMessage: String, exception: RestException) {

    }

    override fun canProcessCallback(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        var datasource = DataSources.getDataSource(FareDataSource::class.java)
        datasource.requestData(this, "2018-02-02T15:43:18+0100")
    }
}
