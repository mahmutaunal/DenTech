package com.mahmutalperenunal.dentech.api

import com.mahmutalperenunal.dentech.model.AppVersion
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitApiVersionControl {

    @POST("")
    fun postVersionNumber(
        @Body version: String
    ): Call<AppVersion>

}