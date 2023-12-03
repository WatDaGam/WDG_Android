package com.watdagam.android.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.watdagam.android.R
import com.watdagam.android.storyList.StoryAdapter
import com.watdagam.android.databinding.ActivityProfileBinding
import com.watdagam.android.storyList.StoryItem
import com.watdagam.android.utils.storage.StorageService

class ProfileActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityProfileBinding
    private val model: ProfileActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        model.getNickname().observe(this) { nickname ->
            viewBinding.toolbarTitle.text = nickname
        }
        model.getPosts().observe(this) { posts ->
            viewBinding.profilePostsNumber.text = posts.toString()
        }
        model.getLikes().observe(this) { likes ->
            viewBinding.profileLikesNumber.text = likes.toString()
        }

        val storyList = ArrayList<StoryItem>()
        val storyAdapter = StoryAdapter(storyList).also {
            it.setHasStableIds(true)
        }
        viewBinding.myStoryList.layoutManager = LinearLayoutManager(this)
        viewBinding.myStoryList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL).also {
            it.setDrawable(AppCompatResources.getDrawable(this, R.color.gray)!!)
        })
        viewBinding.myStoryList.adapter = storyAdapter
        model.getMyStoryList().observe(this) { myStoryList ->
            storyList.clear()
            storyList.addAll(myStoryList)
            storyAdapter.notifyDataSetChanged()
            viewBinding.swipeRefresh.isRefreshing = false
        }

        viewBinding.toolbarBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewBinding.swipeRefresh.setOnRefreshListener {
            model.getMyStoryList().postValue(ArrayList())
            model.loadData(this)
        }

        model.loadData(this.applicationContext)
        model.startLocationTracking(this)
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
}