package com.mahmutalperenunal.dentech.model.auth

import com.google.gson.annotations.SerializedName

data class Login (
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("grant_type") val grant_type: String = "password"

)