package com.mahmutalperenunal.dentech.model.auth

import com.google.gson.annotations.SerializedName

data class Logout (
    @SerializedName("detail") var detail: String
)