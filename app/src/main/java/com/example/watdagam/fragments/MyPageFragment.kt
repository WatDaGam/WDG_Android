package com.example.watdagam.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.watdagam.LoginActivity
import com.example.watdagam.R
import com.example.watdagam.api.ApiService
import com.example.watdagam.api.UserDataSharedPreference
import com.example.watdagam.databinding.FragmentMyPageBinding
import retrofit2.Response

class MyPageFragment : Fragment() {
    private lateinit var viewBinding: FragmentMyPageBinding
    private lateinit var model: MyPageFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyPageBinding.inflate(inflater, container, false)
        model = ViewModelProvider(this)[MyPageFragmentViewModel::class.java]
        model.getProfile().observe(viewLifecycleOwner) {profile ->
            viewBinding.toolbarTitle.text = profile.nickname
            viewBinding.profilePostsNumber.text = profile.post.toString()
            viewBinding.profileLikesNumber.text = profile.likes.toString()
        }

        model.loadProfile(requireContext())

        // Title
        viewBinding.toolbarTitle.text = "MyPage"

        // Menu
        viewBinding.toolbar.overflowIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_menu)
        viewBinding.toolbar.inflateMenu(R.menu.my_page_menu)
        viewBinding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.logoutButton -> {
//                    ApiService.clearUserData()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.withdrawalButton -> {
                    val builder = AlertDialog.Builder(requireContext())
                        .setMessage("정말로 회원 탈퇴하시겠습니까?")
                        .setPositiveButton("네") { _, _ ->
                            val apiService = ApiService.getInstance(requireContext())
//                            apiService.withdrawal(
//                                onSuccess = {_, response -> onWithdrawalSuccess(response)},
//                                onFailure = {_, _ -> onWithdrawalFailure()},
//                            )
                        }
                        .setNegativeButton("아니요", null)
                    val dialog = builder.create()
                    dialog.show()
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

    private fun onWithdrawalSuccess(response: Response<Void>) {
        when (response.code()) {
            200 -> {
                Toast.makeText(requireContext(), "회원 탈퇴 되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
            400 -> {
                Toast.makeText(requireContext(), "로그인 정보가 만료되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Log.e("WDG_API", "Unhandled Response code ${response.code()}")
            }
        }
    }

    private fun onWithdrawalFailure() {
        Toast.makeText(requireContext(), "잠시 후 다시 요청해주세요", Toast.LENGTH_SHORT).show()
    }

}