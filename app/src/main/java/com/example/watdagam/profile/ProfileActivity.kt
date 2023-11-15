package com.example.watdagam.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watdagam.storyList.StoryAdapter
import com.example.watdagam.databinding.ActivityProfileBinding

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

        viewBinding.myStoryList.layoutManager = LinearLayoutManager(this)
        viewBinding.myStoryList.addItemDecoration((DividerItemDecoration(this, LinearLayoutManager.VERTICAL)))
        model.getMyStoryList().observe(this) { storyList ->
            viewBinding.myStoryList.adapter = StoryAdapter(storyList)
        }

        viewBinding.toolbarBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        model.loadData(this.applicationContext)
    }
}