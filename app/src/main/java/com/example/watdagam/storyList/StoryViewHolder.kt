package com.example.watdagam.storyList

import android.transition.AutoTransition
import android.transition.Scene
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.watdagam.R
import com.example.watdagam.api.WDGStoryService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    companion object {
        private const val TAG = "WDG_storyViewHolder"
    }

    private lateinit var sceneRoot: ViewGroup
    private lateinit var foldedScene: Scene
    private lateinit var expandedScene: Scene
    private val transition = AutoTransition().also { it.duration = 100 }

    private lateinit var title: TextView
    private lateinit var location: TextView
    private lateinit var content: TextView
    private lateinit var likes: TextView
    private lateinit var distance: TextView

    fun bind(story: StoryItem) {
        sceneRoot = itemView.findViewById(R.id.scene_root)
        foldedScene = Scene.getSceneForLayout(sceneRoot, R.layout.scene_story_folded, itemView.context)
        expandedScene = Scene.getSceneForLayout(sceneRoot, R.layout.scene_story_expanded, itemView.context)
        if (story.isExpanded) {
            TransitionManager.go(expandedScene)
            sceneRoot.layoutParams.height = (240 * sceneRoot.context.resources.displayMetrics.density).toInt()
        } else {
            TransitionManager.go(foldedScene)
            sceneRoot.layoutParams.height = (90 * sceneRoot.context.resources.displayMetrics.density).toInt()
        }

        rebindView(itemView, story)
    }

    private fun expandItem(story: StoryItem) {
        story.isExpanded = true
        TransitionManager.go(expandedScene, transition)
        val expandRoot = object: Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                sceneRoot.layoutParams.height =
                    ((90 + 150 * interpolatedTime) * sceneRoot.context.resources.displayMetrics.density).toInt()
                sceneRoot.requestLayout()
            }
        }.also {it.duration = 100}
        itemView.startAnimation(expandRoot)
        rebindView(itemView, story)
    }

    private fun foldItem(story: StoryItem) {
        story.isExpanded = false
        TransitionManager.go(foldedScene, transition)
        val foldRoot = object: Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                sceneRoot.layoutParams.height =
                    ((240 - 150 * interpolatedTime) * sceneRoot.context.resources.displayMetrics.density).toInt()
                sceneRoot.requestLayout()
            }
        }.also {it.duration = 150; it.startOffset = 150}
        itemView.startAnimation(foldRoot)
        rebindView(itemView, story)
    }

    private fun rebindView(itemView: View, story: StoryItem) {
        title = itemView.findViewById(R.id.story_title)
        location = itemView.findViewById(R.id.story_location)
        content = itemView.findViewById(R.id.story_content)
        likes = itemView.findViewById(R.id.story_likes)
        distance = itemView.findViewById(R.id.story_distance)

        title.text =  itemView.context.getString(R.string.list_title, story.nickname)
        location.text = String.format("%.3f %.3f", story.latitude, story.longitude)
        content.text = story.content
        likes.text = getLikesString(story.likes)
        distance.text = getDistanceString(story.distance)

        if (story.isExpanded) {
            title.setOnClickListener {
                foldItem(story)
            }
            content.setOnClickListener {
                foldItem(story)
            }
            likes.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = WDGStoryService.addLike(itemView.context, story.id)
                        if (response.isSuccessful) {
                            story.likes += 1
                        } else {
                            throw Exception("Response is not Successful")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "API Failed cause ${e.message} ${e.cause}")
                    }
                }
            }
        } else {
            title.setOnClickListener {
                expandItem(story)
            }
        }
    }

    private fun getLikesString(likes: Int): String {
        return if (likes > 1_000_000) {
            String.format("%.3f M", likes / 1_000_000f)
        } else if (likes > 1_000) {
            String.format("%.3f K", likes / 1_000f)
        } else {
            String.format("%d", likes)
        }
    }

    private fun getDistanceString(distance: Double): String {
        return if (distance > 1_000) {
            String.format("%.3f km", distance / 1_000)
        } else {
            String.format("%.3f m", distance)
        }
    }
}