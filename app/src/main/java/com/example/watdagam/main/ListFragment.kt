package com.example.watdagam.main

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

        model.getCurrentLocation().observe(requireActivity()) { location: Location ->
            model.updateLocationInfo(requireContext(), location)
        }

        model.getUserLocation().observe(requireActivity()) { wdgLocation: WDGLocation ->
            viewBinding.toolbarPlaceName.text = wdgLocation.locationText
            viewBinding.toolbarGps.text = wdgLocation.coordinate
            Toast.makeText(requireContext(), "Location Updated!", Toast.LENGTH_SHORT).show()
        }

        model.reloadLocation(requireActivity())

        return viewBinding.root
    }

}