package com.mahmutalperenunal.dentech.model.auth

import com.google.gson.annotations.SerializedName

data class AuthToken (
    @SerializedName("access_token") val authToken: String
)