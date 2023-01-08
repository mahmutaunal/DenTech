package com.mahmutalperenunal.dentech.api

import com.mahmutalperenunal.dentech.api.auth.RetrofitApiLogin
import com.mahmutalperenunal.dentech.api.auth.RetrofitApiLogout
import com.mahmutalperenunal.dentech.util.Constant.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {

        var mHttpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        var mOkHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(mHttpLoggingInterceptor)
            .build()


        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(mOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiLogin: RetrofitApiLogin by lazy {
        retrofit.create(RetrofitApiLogin::class.java)
    }

    val apiLogout: RetrofitApiLogout by lazy {
        retrofit.create(RetrofitApiLogout::class.java)
    }

    val apiVersionControl: RetrofitApiVersionControl by lazy {
        retrofit.create(RetrofitApiVersionControl::class.java)
    }

    val apiSayim: RetrofitApiSayim by lazy {
        retrofit.create(RetrofitApiSayim::class.java)
    }

    val apiStokSorgu: RetrofitApiStokSorgulama by lazy {
        retrofit.create(RetrofitApiStokSorgulama::class.java)
    }

}