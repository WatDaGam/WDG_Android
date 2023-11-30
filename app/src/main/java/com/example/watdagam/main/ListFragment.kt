package com.example.watdagam.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watdagam.R
import com.example.watdagam.databinding.FragmentListBinding
import com.example.watdagam.storyList.StoryAdapter
import com.example.watdagam.storyList.StoryItem
import com.google.android.gms.ads.AdRequest

class ListFragment : Fragment() {
    private lateinit var viewBinding: FragmentListBinding
    private val model: ListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentListBinding.inflate(inflater, container, false)
        val storyList = ArrayList<StoryItem>()

        model.getCurrentAddress().observe(viewLifecycleOwner) { address ->
            viewBinding.toolbarTitle.text = address.featureName
            viewBinding.toolbarLocation.text = String.format("%.5f %.5f", address.latitude, address.longitude)
        }

        val storyAdapter = StoryAdapter(storyList).also {
            it.setHasStableIds(true)
        }
        viewBinding.storyList.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.storyList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL).also {
            it.setDrawable(AppCompatResources.getDrawable(requireContext(), R.color.gray)!!)
        })
        viewBinding.storyList.adapter = storyAdapter
        model.getStoryItemList().observe(viewLifecycleOwner) { list ->
            storyList.clear()
            storyList.addAll(list)
//            Log.d("WDG_listFragment", storyList.toString())
            storyAdapter.notifyDataSetChanged()
            viewBinding.swipeRefresh.isRefreshing = false
        }

        viewBinding.swipeRefresh.setOnRefreshListener {
            model.getStoryItemList().postValue(ArrayList())
            model.reloadLocation(requireActivity())
        }

        val adRequest = AdRequest.Builder().build()
        viewBinding.adView.loadAd(adRequest)


        model.startLocationTracking(requireActivity(), viewLifecycleOwner)

        return viewBinding.root
    }
}