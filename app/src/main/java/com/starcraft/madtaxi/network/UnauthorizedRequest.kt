package com.starcraft.madtaxi.network


abstract class UnauthorizedRequest<RESPONSE> : BaseRequest<RESPONSE>() {

    override fun getHeaders(): MutableMap<String, List<String>> {
        val headers : MutableMap<String, List<String>> = super.getHeaders()

        // TODO add real headers

        return headers
    }

}