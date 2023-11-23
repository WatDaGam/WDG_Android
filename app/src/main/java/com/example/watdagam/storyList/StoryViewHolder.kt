package com.example.watdagam.storyList

import android.animation.ValueAnimator
import android.transition.Scene
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.watdagam.R
import com.example.watdagam.api.WDGStoryService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private lateinit var container: ConstraintLayout
    private lateinit var title: TextView
    private lateinit var location: TextView
    private lateinit var content: TextView
    private lateinit var likesContainer: LinearLayoutCompat
    private lateinit var likesAnimation: LottieAnimationView
    private lateinit var likesNum: TextView
    private lateinit var distanceContainer: LinearLayoutCompat
    private lateinit var distanceIcon: ImageView
    private lateinit var distanceNum: TextView

    private val likeAnimator = ValueAnimator.ofFloat(0.0f, 1.5f).setDuration(1000)
    fun bind(story: StoryItem) {
        bindViews(story)
        val sceneRoot: ViewGroup = itemView.findViewById(R.id.scene_root)
        val expandedScene = Scene.getSceneForLayout(sceneRoot, R.layout.scene_story_expanded, itemView.context).also {
            it.setEnterAction {
                bindViews(story)
                addLikePressedListener(story)
            }
        }
        val foldedScene = Scene.getSceneForLayout(sceneRoot, R.layout.scene_story_folded, itemView.context).also {
            it.setEnterAction {
                bindViews(story)
            }
        }

        if (story.tooFar) {
            foldedScene.enter()
        }
        itemView.setOnClickListener {
            if (story.tooFar) {
                Toast.makeText(itemView.context, "메세지를 확인하려면 30m이내로 접근해주세요", Toast.LENGTH_SHORT).show()
            } else {
                if (story.isExpanded) {
                    story.isExpanded = false
                    TransitionManager.go(foldedScene, null)
                } else {
                    story.isExpanded = true
                    TransitionManager.go(expandedScene)
                }
            }
        }
    }

    private fun bindViews(story: StoryItem) {
        container = itemView.findViewById(R.id.container)
        title = itemView.findViewById(R.id.story_title)
        location = itemView.findViewById(R.id.story_location)
        content = itemView.findViewById(R.id.story_content)
        likesContainer = itemView.findViewById(R.id.story_likes)
        likesAnimation = itemView.findViewById(R.id.story_likes_anim)
        likesNum = itemView.findViewById(R.id.story_likes_num)
        distanceContainer = itemView.findViewById(R.id.story_distance_container)
        distanceIcon = itemView.findViewById(R.id.story_distance_icon)
        distanceNum = itemView.findViewById(R.id.story_distance_num)

        title.text = story.title
        location.text = story.location
        content.text = story.content
        likesAnimation.progress = if (story.isExpanded) 1.0f else 0.0f
        likesNum.text = story.likes.toString()
        distanceNum. text = story.distance
        container.alpha = if (story.tooFar) 0.3f else 1f
    }

    private fun addLikePressedListener(story: StoryItem) {
        likeAnimator.addUpdateListener { animation: ValueAnimator ->
            likesAnimation.progress = animation.animatedValue as Float
        }
        likesContainer.setOnClickListener {
            likeAnimator.start()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = WDGStoryService.addLike(itemView.context, story.id)
                    if (response.isSuccessful) {
                        if (response.body() != null && story.likes != response.body()!!.likeNum) {
                            story.likes = response.body()!!.likeNum
                            likesNum.text = story.likes.toString()
                        }
                    } else {
                        throw Exception("Response is not successful")
                    }
                } catch (e: Exception) {
                    Log.e("WDG_storyViewHolder", "add like failed cause ${e.message} ${e.cause}")
                }
            }
        }
    }
}