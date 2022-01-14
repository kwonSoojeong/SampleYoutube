package com.crystal.sampleyoutube

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.crystal.sampleyoutube.databinding.FragmentPlayerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs

class PlayerFragment : Fragment(R.layout.fragment_player) {
    private var binding: FragmentPlayerBinding? = null
    private val videoListAdapter: VideoListAdapter by lazy {
        VideoListAdapter { sourceUrl, title ->
            play(sourceUrl, title)
        }
    }
    private lateinit var player: ExoPlayer


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding
        initMotionLayoutEvent()
        initRecyclerView()
        initPlayer(fragmentPlayerBinding)
        initControlButton(fragmentPlayerBinding)
    }

    private fun initMotionLayoutEvent() {
        binding?.fragmentPlayerMotionLayout?.setTransitionListener(object : TransitionAdapter() {
            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                //fragmentPlayerMotionLayout이 동작할 때, Main activity에 있는 Motion을 실행시킬것임
                binding.let {
                    (activity as MainActivity).also { mainActivity ->
                        mainActivity.findViewById<MotionLayout>(R.id.mainMotionLayout).progress =
                            abs(progress)
                    }
                }
            }
        })
    }

    private fun initRecyclerView() {
        binding?.let {
            it.recyclerView.adapter = videoListAdapter
            it.recyclerView.layoutManager = LinearLayoutManager(context)
            getVideoList()
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

    private fun initPlayer(fragmentPlayerBinding: FragmentPlayerBinding) {
        context?.let {
            player = ExoPlayer.Builder(it).build()
        }
        fragmentPlayerBinding.playerView.player = player

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                binding?.let {
                    if (isPlaying) {
                        //정지버튼
                        it.bottomPlayerControlButton.setImageResource(R.drawable.ic_pause)
                    } else {
                        // 재생버튼
                        it.bottomPlayerControlButton.setImageResource(R.drawable.ic_play)
                    }
                }
            }
        })
    }

    private fun initControlButton(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.bottomPlayerControlButton.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
    }

    fun play(url: String, title: String) {
        context?.let {
            val dataSourceFactory = DefaultDataSourceFactory(it)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
            player.setMediaSource(mediaSource)
            player.prepare()//데이터를 가지오기시작함
            player.play()//정상 실행
        }
        binding?.let {
            it.fragmentPlayerMotionLayout.transitionToEnd()
            it.titleTextView.text = title
        }
    }

    override fun onStop() {
        super.onStop()
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        player.release()
    }
}