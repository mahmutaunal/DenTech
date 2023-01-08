package com.mahmutalperenunal.dentech.model

import com.google.gson.annotations.SerializedName

data class AppVersion (
    @SerializedName("version_number") var versionNumber: String
        )