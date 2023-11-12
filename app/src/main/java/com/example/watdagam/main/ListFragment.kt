package com.example.watdagam.main

import android.location.Address
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watdagam.data.Story
import com.example.watdagam.StoryAdapter
import com.example.watdagam.data.StoryDto
import com.example.watdagam.databinding.FragmentListBinding
import java.util.Date

class ListFragment : Fragment() {
    private lateinit var viewBinding: FragmentListBinding
    private val model: MainActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private val storyDtoList = arrayListOf(
        StoryDto(Date(0), 32.1231231, 127.12312312,
            "yback", 0, 3, "This is a content1, This is a content1, This is a.", 0),
        StoryDto(Date(0), 32.1231231, 127.12312312,
            "yback", 1, 3, "This is a content2", 0),
        StoryDto(Date(0), 32.1231231, 127.12312312,
            "yback", 2, 3, "This is a content3", 0),
        StoryDto(Date(0), 32.1231231, 127.12312312,
            "yback", 3, 3, "This is a content4", 0),
        StoryDto(Date(0), 32.1231231, 127.12312312,
            "yback", 4, 3, "This is a content5", 0),
        StoryDto(Date(0), 32.1231231, 127.12312312,
            "yback", 5, 3, "This is a content6", 0),
        StoryDto(Date(0), 32.1231231, 127.12312312,
            "yback", 6, 3, "This is a content7", 0),
        StoryDto(Date(0), 32.1231231, 127.12312312,
            "yback", 7, 3, "This is a content8", 0),
        StoryDto(Date(0), 32.1231231, 127.12312312,
            "yback", 8, 3, "This is a content9", 0),
        StoryDto(Date(0), 32.1231231, 127.12312312,
            "yback", 9, 3, "This is a content10", 0),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentListBinding.inflate(inflater, container, false)

        model.getListAddress().observe(requireActivity()) { address: Address ->
            viewBinding.toolbarPlaceName.text =
                if (!address.thoroughfare.isNullOrBlank()) {
                    address.thoroughfare
                } else if (!address.subLocality.isNullOrBlank()) {
                    address.subLocality
                } else if (!address.locality.isNullOrBlank()) {
                    address.locality
                } else if (!address.subAdminArea.isNullOrBlank()) {
                    address.subAdminArea
                } else if (!address.adminArea.isNullOrBlank()) {
                    address.adminArea
                } else {
                    address.countryName
                }
            val gpsText = "${address.latitude} ${address.longitude}"
            viewBinding.toolbarGps.text = gpsText
        }

        val storyList = mutableListOf<Story>()
        for (story in storyDtoList) {
            storyList.add(Story(
                story.createdAt,
                story.lati,
                story.longi,
                story.nickname,
                story.id,
                story.userId,
                story.content,
                story.likeNum,
                0.0
            ))
        }
        viewBinding.storyList.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.storyList.adapter = StoryAdapter(storyList)
        viewBinding.storyList.addItemDecoration((DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)))

        model.reloadLocation(requireActivity())

        return viewBinding.root
    }

}