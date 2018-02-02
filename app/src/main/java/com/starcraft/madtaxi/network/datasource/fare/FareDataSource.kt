package com.starcraft.madtaxi.network.datasource.fare

import com.starcraft.madtaxi.network.BaseRequest
import com.starcraft.madtaxi.network.datasource.MultipleValueDataSource
import com.starcraft.madtaxi.network.fare.request.FareRequest
import com.starcraft.madtaxi.network.fare.response.FareResponse

class FareDataSource : MultipleValueDataSource<String, FareResponse>() {
    override fun getDefaultRequest(date: String): BaseRequest<FareResponse> {
        return FareRequest(date)
    }

    override fun getDefaultRequest(): BaseRequest<FareResponse> {
        return FareRequest("")
    }

    override fun getCacheTtlInMillis(): Long {
        return -1
    }
}