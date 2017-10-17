package de.cronosx.mumblebot.api

import retrofit2.Call
import retrofit2.http.GET

interface MumbleBotApi {
    @GET("/authorized")
    fun isAuthorized( ): Call<BaseResponse>
}
