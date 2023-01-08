package com.mahmutalperenunal.dentech.api.auth

import com.mahmutalperenunal.dentech.model.auth.AuthToken
import com.mahmutalperenunal.dentech.model.auth.Login
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetrofitApiLogin {

    @POST("")
    fun postLogin(
        @Body logIn: Login
    ): Call<AuthToken>

}