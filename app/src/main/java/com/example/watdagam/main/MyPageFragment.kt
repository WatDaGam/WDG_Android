package com.example.watdagam.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.watdagam.databinding.FragmentMyPageBinding
import com.example.watdagam.profile.ProfileActivity

class MyPageFragment : Fragment() {
    private lateinit var viewBinding: FragmentMyPageBinding
    private val model: MainActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentMyPageBinding.inflate(inflater, container, false)

        viewBinding.buttonProfile.setOnClickListener{
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        viewBinding.buttonLogout.setOnClickListener{
            model.logout(requireContext())
        }

        viewBinding.buttonWithdraw.setOnClickListener{
            model.withdraw(requireContext())
        }

        return viewBinding.root
    }
}