package com.example.watdagam.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.watdagam.R
import com.example.watdagam.databinding.ActivityMainBinding

private const val TAG_LIST = "list_fragment"
private const val TAG_MY_PAGE = "my_page_fragment"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val model: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_LIST, ListFragment())

        val fineLocationPermission = ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (fineLocationPermission == PackageManager.PERMISSION_DENIED ||
            coarseLocationPermission == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "글을 남기기 위해서는 자세한 위치 사용 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            model.requestLocationPermissions(this)
        } else {
            model.startLocationTracking(this)
        }
        model.reloadLocation(this)

        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.listFragment -> {
                    model.reloadLocation(this)
                    model.loadStoryList(this)
                    setFragment(TAG_LIST, ListFragment())
                }
                R.id.postFragment -> model.startPostActivity(this)
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