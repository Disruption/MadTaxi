package com.starcraft.madtaxi.network.login.response

import com.google.gson.annotations.SerializedName


class LoginResponse {

    @SerializedName("token")
    lateinit var mToken: String
    @SerializedName("user_id")
    lateinit var user: String

}