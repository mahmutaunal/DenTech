package com.mahmutalperenunal.dentech.model

import com.google.gson.annotations.SerializedName

data class SayimModel (
    @SerializedName("sayimNo") var sayimNo: String,
    @SerializedName("materialBarcode") var materialBarcode: String,
    @SerializedName("lotBatchNo") var lotBatchNo: String,
    @SerializedName("configurationNo") var configurationNo: String,
    @SerializedName("serialNo") var serialNo: String,
    @SerializedName("locationNo") var locationBarcode: String,
    @SerializedName("amount") var amount: String,
    @SerializedName("date") var date: String,
    var isSelected: Boolean
        )