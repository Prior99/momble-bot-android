package de.cronosx.mumblebot.api

import android.util.Base64
import android.util.Log.*
import kotlinx.coroutines.experimental.async
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*


class Api {
    private val mumbleBotApi: MumbleBotApi
    private var token: String? = null
    private val loggedIn: Boolean
        get() = token != null

    init {
        val httpClient = OkHttpClient.Builder()
                .addInterceptor(Interceptor { chain ->
                    val builder = chain.request().newBuilder()
                    if (loggedIn) {
                        builder.addHeader("authorization", "Basic $token")
                    }
                    chain.proceed(builder.build())
                })
                .addInterceptor(Interceptor { chain ->
                    val request = chain.request()
                    val response = chain.proceed(request)
                    val body = response.peekBody(Long.MAX_VALUE)
                    v("Api", "Call:${request.method()} ${request.url()} " +
                            "with ${request.body()} => " +
                            "${response.code()}: ${body.string()}"
                    )
                    response
                })
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.mumble.92k.de")
                .addConverterFactory(MoshiConverterFactory.create())
                .client(httpClient)
                .build()
        mumbleBotApi = retrofit.create(MumbleBotApi::class.java)
    }

    fun createToken(username: String, password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(password.toByteArray(Charset.forName("UTF-8")))
        val bytes = messageDigest.digest()
        val sb = StringBuffer()
        val formatter = Formatter(sb)
        bytes.forEach{ formatter.format("%02x", it) }
        val encryptedPassword = sb.toString()
        val unencodedToken = "$username:$encryptedPassword"
        return Base64.encodeToString(unencodedToken.toByteArray(Charset.forName("UTF-8")), Base64.NO_WRAP)

    }

    fun attemptLogin(token: String): Boolean {
        this.token = token
        val success = mumbleBotApi.isAuthorized().execute().code() === 200
        if (!success) {
            this.token = null
        }
        return success
    }

    fun attemptLogin(username: String, password: String): Boolean = attemptLogin(createToken(username, password))
}
