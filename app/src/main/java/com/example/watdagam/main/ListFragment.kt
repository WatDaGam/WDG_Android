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
import com.example.watdagam.storyList.StoryAdapter
import com.example.watdagam.databinding.FragmentListBinding

class ListFragment : Fragment() {
    private lateinit var viewBinding: FragmentListBinding
    private val model: MainActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentListBinding.inflate(inflater, container, false)

        model.getLastAddress().observe(requireActivity()) { address: Address ->
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

        viewBinding.storyList.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.storyList.addItemDecoration((DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)))
        model.getStoryList().observe(requireActivity()) { list ->
            viewBinding.storyList.adapter = StoryAdapter(list)
        }

        return viewBinding.root
    }

}