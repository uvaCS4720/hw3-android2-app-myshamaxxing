package edu.nd.pmcburne.hwapp.one

import edu.nd.pmcburne.hwapp.one.data.ApiResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface BasketballApiService {

    @GET("scoreboard/basketball-{gender}/d1/{year}/{month}/{day}")
    suspend fun getScoreboard(
        @Path("gender") gender: String,
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String
    ): ApiResponse
}

object RetrofitInstance {
    private const val BASE_URL = "https://ncaa-api.henrygd.me/"

    val api: BasketballApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BasketballApiService::class.java)
    }
}
