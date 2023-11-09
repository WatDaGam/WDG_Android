package com.example.watdagam.MyPageFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.watdagam.R
import com.example.watdagam.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {
    private lateinit var viewBinding: FragmentMyPageBinding
    private lateinit var model: MyPageFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentMyPageBinding.inflate(inflater, container, false)
        model = ViewModelProvider(this)[MyPageFragmentViewModel::class.java]
        model.getProfile().observe(viewLifecycleOwner) {profile ->
            viewBinding.toolbarTitle.text = profile.nickname
            viewBinding.profilePostsNumber.text = profile.post.toString()
            viewBinding.profileLikesNumber.text = profile.likes.toString()
        }

        model.loadProfile(requireContext())

        // Menu
        viewBinding.toolbar.overflowIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_menu)
        viewBinding.toolbar.inflateMenu(R.menu.my_page_menu)
        viewBinding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.logoutButton -> {
                    model.logout(requireContext())
                    true
                }
                R.id.withdrawalButton -> {
                    val builder = AlertDialog.Builder(requireContext())
                        .setMessage("정말로 회원 탈퇴하시겠습니까?")
                        .setPositiveButton("네") { _, _ ->
                            model.withdraw(requireContext())
                        }
                        .setNegativeButton("아니요", null)
                    builder.create().show()
                    true
                }
                else -> false
            }
        }

        // Back
        viewBinding.toolbar.setNavigationOnClickListener { view ->
            TODO("move backward")
        }
        return viewBinding.root
    }


}