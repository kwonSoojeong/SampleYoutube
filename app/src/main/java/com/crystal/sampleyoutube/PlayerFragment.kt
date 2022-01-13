package com.crystal.sampleyoutube

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.crystal.sampleyoutube.databinding.FragmentPlayerBinding
import kotlin.math.abs

class PlayerFragment : Fragment(R.layout.fragment_player) {
    private var binding: FragmentPlayerBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding
        initMotionLayoutEvent()
        initRecyclerView()
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
            val adapter = VideoAdapter()
            it.recyclerView.adapter = adapter
            it.recyclerView.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}