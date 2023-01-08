package com.mahmutalperenunal.dentech.api

import com.mahmutalperenunal.dentech.model.SayimModel
import retrofit2.Call
import retrofit2.http.*

interface RetrofitApiSayim {

    @GET("")
    fun getSayimData(
        @Header("Authorization") auth: String
    ): Call<List<SayimModel>>

    @POST("")
    fun postSayimData(
        @Header("Authorization") auth: String,
        @Body sayimModel: SayimModel
    ): Call<SayimModel>

    @PUT("")
    fun putSayimData(
        @Header("Authorization") auth: String,
        @Path("id") id: Int,
        @Body sayimModel: SayimModel
    ): Call<SayimModel>



}