package com.starcraft.madtaxi.network

import com.starcraft.madtaxi.Constants
import java.util.*


abstract class AuthorizedRequest<RESPONSE> : BaseRequest<RESPONSE>() {
    private val AUTHORIZATION_HEADER_PARAM = "Authorization"
    private val TOKEN_PREFIX = "Token"
    private val CLIENT_ID_HEADER_PARAM = "X-GTAXI-UUID"

    override fun getHeaders(): MutableMap<String, List<String>> {

        val headers = super.getHeaders()
        headers.put(AUTHORIZATION_HEADER_PARAM,
                Collections.singletonList(TOKEN_PREFIX + Constants.API_KEY))
        headers.put(CLIENT_ID_HEADER_PARAM,
                Collections.singletonList(Constants.CLIENT_ID))

        return headers
    }
}