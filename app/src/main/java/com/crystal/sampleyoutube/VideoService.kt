package com.crystal.sampleyoutube

import retrofit2.Call
import retrofit2.http.GET

interface VideoService {
    //https://run.mocky.io/v3/b089b27d-8e56-4c31-8c28-231ea7bc8efd
    @GET("/v3/b089b27d-8e56-4c31-8c28-231ea7bc8efd")
    fun listVideos(): Call<VideoDto>

}