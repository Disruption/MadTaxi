package com.starcraft.madtaxi.network

interface BaseRequestListener<in RESPONSE>{

    fun onRequestSuccessful(response: RESPONSE?)

    fun onRequestFailed(exception: RestException)

}