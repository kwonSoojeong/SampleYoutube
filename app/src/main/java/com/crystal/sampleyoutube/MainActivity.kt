package com.crystal.sampleyoutube

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var videoListAdapter: VideoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PlayerFragment())
            .commit()
        getVideoList()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        videoListAdapter = VideoListAdapter(
            callback = { url, title -> //playFragment motion 펼쳐지고 play, view update!
                supportFragmentManager.fragments.find { it is PlayerFragment }?.let {
                    (it as PlayerFragment).play(url, title)
                }
            }
        )
        findViewById<RecyclerView>(R.id.mainRecyclerView).apply {
            adapter = videoListAdapter
            layoutManager= LinearLayoutManager(context)
        }
    }


    private fun getVideoList() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also {
            it.listVideos().enqueue(object : Callback<VideoDto> {
                override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                    if (response.isSuccessful.not()) {
                        Log.e("MainActivity", "response fail")
                        return
                    }
                    response.body()?.let { videoDto ->
//                        Log.d("MainActivity", dto.toString())
                        videoListAdapter.submitList(videoDto.videos)
                    }
                }

                override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                    Log.e("MainActivity", "response fail")
                }

            })
        }

    }
}