package com.example.watdagam.storyList

import android.animation.ValueAnimator
import android.transition.Scene
import android.transition.TransitionManager
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

    private val likeAnimator = ValueAnimator.ofFloat(0.0f, 0.5f).setDuration(500)
    private val unlikeAnimator = ValueAnimator.ofFloat(0.5f, 1.0f).setDuration(500)
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

        itemView.setOnClickListener {
            if (story.tooFar) {
                foldedScene.enter()
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
        likesNum.text = story.likes.toString()
        distanceNum. text = story.distance
        container.alpha = if (story.tooFar) 0.3f else 1f
        likesAnimation.progress = if (story.hasLikeFromMe) 0.5f else 0.0f
    }

    private fun addLikePressedListener(story: StoryItem) {
        unlikeAnimator.addUpdateListener { animation: ValueAnimator ->
            likesAnimation.progress = animation.animatedValue as Float
        }
        likeAnimator.addUpdateListener { animation: ValueAnimator ->
            likesAnimation.progress = animation.animatedValue as Float
        }
        likesContainer.setOnClickListener {
            if (story.hasLikeFromMe) {
                likesNum.text = (--story.likes).toString()
                unlikeAnimator.start()
                story.hasLikeFromMe = false
                postUnlikeApi(story)
            } else {
                likesNum.text = (++story.likes).toString()
                likeAnimator.start()
                story.hasLikeFromMe = true
                postLikeApi(story)
            }
        }
    }

    private fun postLikeApi(story: StoryItem) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = WDGStoryService.addLike(itemView.context, story.id);
//        }
        Toast.makeText(itemView.context, "I like it!", Toast.LENGTH_SHORT).show()
    }

    private fun postUnlikeApi(story: StoryItem) {
        Toast.makeText(itemView.context, "I don't like it!", Toast.LENGTH_SHORT).show()
    }
}