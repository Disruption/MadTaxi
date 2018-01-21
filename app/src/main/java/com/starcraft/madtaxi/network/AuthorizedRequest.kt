package com.starcraft.madtaxi.network

import com.starcraft.madtaxi.network.datastore.SharedPreferencesController
import java.util.*


abstract class AuthorizedRequest<RESPONSE> : BaseRequest<RESPONSE>() {
    private val AUTHORIZATION_HEADER_PARAM = "Authorization"
    private val TOKEN_PREFIX = "B2BToken "

    override fun getHeaders(): MutableMap<String, List<String>> {

        val headers = super.getHeaders()

        headers.put(AUTHORIZATION_HEADER_PARAM,
                Collections.singletonList(TOKEN_PREFIX + SharedPreferencesController.getInstance().getAuthToken()))

        return headers
    }
}