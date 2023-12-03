package com.watdagam.android.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.watdagam.android.R
import com.watdagam.android.databinding.ActivityMainBinding
import com.watdagam.android.post.PostActivity
import com.watdagam.android.utils.storage.StorageService
import com.google.android.gms.ads.MobileAds

private const val TAG_LIST = "list_fragment"
private const val TAG_MY_PAGE = "my_page_fragment"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_LIST, ListFragment())

        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.listFragment -> {
                    setFragment(TAG_LIST, ListFragment())
                    binding.mainFrameLayout.findViewById<RecyclerView>(R.id.story_list)?.smoothScrollToPosition(0)
                    true
                }
                R.id.postFragment -> {
                    startActivity(Intent(this, PostActivity::class.java))
                    false
                }
                R.id.myPageFragment -> {
                    setFragment(TAG_MY_PAGE, MyPageFragment())
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val profileService = StorageService.getInstance(this).getProfileService()
        val reportedStories = profileService.getReportedStories()
        reportedStories.forEach { content: String ->
            val dialog = AlertDialog.Builder(this)
                .setMessage("신고가 누적되어 메세지가 삭제되었습니다. 삭제된 메세지: $content")
                .setPositiveButton("확인", null)
                .create()
            dialog.show()
        }
        reportedStories.clear()
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null) {
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val list = manager.findFragmentByTag(TAG_LIST)
        val myPage = manager.findFragmentByTag(TAG_MY_PAGE)

        if (list != null) {
            fragTransaction.hide(list)
        }
        if (myPage != null) {
            fragTransaction.hide(myPage)
        }

        when (tag) {
            TAG_LIST -> {
                if (list != null)
                    fragTransaction.show(list)
            }
            TAG_MY_PAGE -> {
                if (myPage != null)
                    fragTransaction.show(myPage)
            }
        }
        fragTransaction.commitAllowingStateLoss()
    }
}