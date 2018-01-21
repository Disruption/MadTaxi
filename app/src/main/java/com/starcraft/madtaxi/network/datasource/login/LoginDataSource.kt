package com.starcraft.madtaxi.network.datasource.login

import com.starcraft.madtaxi.network.BaseRequest
import com.starcraft.madtaxi.network.datasource.UniqueValueDataSource
import com.starcraft.madtaxi.network.login.request.LoginRequest
import com.starcraft.madtaxi.network.login.response.LoginResponse

class LoginDataSource : UniqueValueDataSource<LoginResponse>() {

    override fun getDefaultRequest(): BaseRequest<LoginResponse> {
        return LoginRequest("", "")
    }

    override fun getCacheTtlInMillis(): Long {
        return -1
    }
}