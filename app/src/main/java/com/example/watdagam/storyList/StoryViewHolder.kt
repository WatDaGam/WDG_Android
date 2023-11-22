package com.example.watdagam.storyList

import android.transition.Scene
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.watdagam.R
import com.example.watdagam.api.WDGStoryService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private lateinit var title: TextView
    private lateinit var location: TextView
    private lateinit var content: TextView
    private lateinit var likes: TextView
    private lateinit var distance: TextView
    fun bind(story: StoryItem) {
        title = itemView.findViewById(R.id.story_title)
        location = itemView.findViewById(R.id.story_location)
        content = itemView.findViewById(R.id.story_content)
        likes = itemView.findViewById(R.id.story_likes)
        distance = itemView.findViewById(R.id.story_distance)

        title.text = story.title
        location.text = story.location
        content.text = story.content
        likes.text = story.likes
        distance.text = story.distance

        val sceneRoot: ViewGroup = itemView.findViewById(R.id.scene_root)
        val expandedScene = Scene.getSceneForLayout(sceneRoot, R.layout.scene_story_expanded, itemView.context).also {
            it.setEnterAction {
                title = itemView.findViewById(R.id.story_title)
                location = itemView.findViewById(R.id.story_location)
                content = itemView.findViewById(R.id.story_content)
                likes = itemView.findViewById(R.id.story_likes)
                distance = itemView.findViewById(R.id.story_distance)

                title.text = story.title
                location.text = story.location
                content.text = story.content
                likes.text = story.likes
                distance.text = story.distance
                likes.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val likeNum = story.likes.toInt()
                            story.likes = (likeNum + 1).toString()
                            val response = WDGStoryService.addLike(itemView.context, story.id)
                            if (response.isSuccessful.not()) {
                                throw Exception("Response is not Successful")
                            }
                        } catch (e: Exception) {
                            Log.e("WDG_storyViewHolder", "like failed cause ${e.message} ${e.cause}")
                        }
                    }
                }
            }
        }
        val foldedScene = Scene.getSceneForLayout(sceneRoot, R.layout.scene_story_folded, itemView.context).also {
            it.setEnterAction {
                title = itemView.findViewById(R.id.story_title)
                location = itemView.findViewById(R.id.story_location)
                content = itemView.findViewById(R.id.story_content)
                likes = itemView.findViewById(R.id.story_likes)
                distance = itemView.findViewById(R.id.story_distance)

                title.text = story.title
                location.text = story.location
                content.text = story.content
                likes.text = story.likes
                distance.text = story.distance
            }
        }

        itemView.setOnClickListener {
            if (story.tooFar) {
                Toast.makeText(itemView.context, "메세지를 확인하려면 30m이내로 접근해주세요", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(itemView.context, "view click", Toast.LENGTH_SHORT).show()
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
}