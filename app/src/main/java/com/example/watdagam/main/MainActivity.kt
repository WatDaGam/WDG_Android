package com.example.watdagam.main

import android.location.Location
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.watdagam.R
import com.example.watdagam.databinding.ActivityMainBinding

private const val TAG_LIST = "list_fragment"
private const val TAG_POST = "post_fragment"
private const val TAG_MY_PAGE = "my_page_fragment"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val model: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_LIST, ListFragment())

        model.getCurrentLocation().observe(this) { currentLocation: Location ->
            val lastLocationLiveData = model.getLastLocation()
            if (lastLocationLiveData.value == null) {
                model.updateLocationInfo(this.applicationContext, currentLocation)
            } else {
                val distance = currentLocation.distanceTo(lastLocationLiveData.value!!)
                if (distance > 30.0) {
                    model.updateLocationInfo(this.applicationContext, currentLocation)
                }
//                Toast.makeText(this.applicationContext, "distance: ${distance}m", Toast.LENGTH_SHORT).show()
            }
        }

        model.startLocationTracking(this)

        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.listFragment -> setFragment(TAG_LIST, ListFragment())
                R.id.postFragment -> setFragment(TAG_POST, PostFragment())
                R.id.myPageFragment -> setFragment(TAG_MY_PAGE, MyPageFragment())
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null) {
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val list = manager.findFragmentByTag(TAG_LIST)
        val post = manager.findFragmentByTag(TAG_POST)
        val myPage = manager.findFragmentByTag(TAG_MY_PAGE)

        if (list != null) {
            fragTransaction.hide(list)
        }
        if (post != null) {
            fragTransaction.hide(post)
        }
        if (myPage != null) {
            fragTransaction.hide(myPage)
        }

        when (tag) {
            TAG_LIST -> {
                if (list != null)
                    fragTransaction.show(list)
            }
            TAG_POST -> {
                if (post != null)
                    fragTransaction.show(post)
            }
            TAG_MY_PAGE -> {
                if (myPage != null)
                    fragTransaction.show(myPage)

            }
        }
        fragTransaction.commitAllowingStateLoss()
    }
}