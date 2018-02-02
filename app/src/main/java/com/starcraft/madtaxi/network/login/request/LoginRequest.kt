package com.starcraft.madtaxi.network.login.request

import com.starcraft.madtaxi.network.Endpoints
import com.starcraft.madtaxi.network.UnauthorizedRequest
import com.starcraft.madtaxi.network.login.response.LoginResponse

class LoginRequest(username: String, password: String) : UnauthorizedRequest<LoginResponse>() {

    val mUsername: String = username
    val mPassword: String = password

    override fun getEndpointUrl(): String {
        return getDomain() + Endpoints.LOGIN
    }

    override fun getResponseClass(): Class<LoginResponse> {
        return LoginResponse::class.java
    }

    override fun getBody(): String? {
        val body: MutableMap<String, String> = HashMap()
        body.put("username", mUsername)
        body.put("password", mPassword)
        return gson.toJson(body)
    }
}