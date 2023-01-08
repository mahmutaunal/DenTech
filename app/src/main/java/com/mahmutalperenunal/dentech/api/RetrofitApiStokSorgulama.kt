package com.mahmutalperenunal.dentech.api

import com.mahmutalperenunal.dentech.model.SayimModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface RetrofitApiStokSorgulama {

    @GET("")
    fun getStokSorguData(
        @Header("Authorization") auth: String,
        @Path("") materialCode: String,
        @Path("") locationCode: String
    ): Call<List<SayimModel>>

}