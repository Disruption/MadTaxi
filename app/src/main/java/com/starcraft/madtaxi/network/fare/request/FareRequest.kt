package com.starcraft.madtaxi.network.fare.request

import com.starcraft.madtaxi.network.AuthorizedRequest
import com.starcraft.madtaxi.network.Endpoints
import com.starcraft.madtaxi.network.fare.response.FareResponse

const val DATE_START = "FechaEntradaEnVigor"

class FareRequest(date: String) : AuthorizedRequest<FareResponse>() {

    val mDate: String = date

    init {
        addRequestParam(DATE_START, mDate)
    }

    override fun getEndpointUrl(): String {
        return getDomain() + Endpoints.GET_FARES
    }

    override fun getResponseClass(): Class<FareResponse> {
        return FareResponse::class.java
    }
}