package com.mahmutalperenunal.dentech.api.auth

import com.mahmutalperenunal.dentech.model.auth.Logout
import retrofit2.Call
import retrofit2.http.POST

interface RetrofitApiLogout {

    @POST("")
    fun postLogout(): Call<Logout>

}