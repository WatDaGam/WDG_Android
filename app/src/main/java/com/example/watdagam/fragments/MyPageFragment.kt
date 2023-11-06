package com.example.watdagam.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.watdagam.LoginActivity
import com.example.watdagam.R
import com.example.watdagam.api.ApiService
import com.example.watdagam.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {
    private lateinit var viewBinding: FragmentMyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyPageBinding.inflate(inflater, container, false)
        // Title
        viewBinding.toolbarTitle.text = "MyPage"

        // Menu
        viewBinding.toolbar.overflowIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_menu)
        viewBinding.toolbar.inflateMenu(R.menu.my_page_menu)
        viewBinding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.logoutButton -> {
                    ApiService.clearUserData()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.withdrawalButton -> {
                    TODO("call api")
                }
                else -> {
                    TODO("call api")
                }
            }
        }

        // Back
        viewBinding.toolbar.setNavigationOnClickListener { view ->
            TODO("move backward")
        }
        return viewBinding.root
    }

}