package com.example.watdagam.main

import android.location.Address
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.watdagam.databinding.FragmentPostBinding

class PostFragment : Fragment() {
    private lateinit var viewBinding: FragmentPostBinding
    private val model: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentPostBinding.inflate(inflater, container, false)

        model.getPostAddress().observe(requireActivity()) { address: Address ->
            viewBinding.locationName.text =
                if (!address.thoroughfare.isNullOrBlank()) {
                    address.thoroughfare
                } else if (!address.subLocality.isNullOrBlank()) {
                    address.subLocality
                } else if (!address.locality.isNullOrBlank()) {
                    address.locality
                } else if (!address.subAdminArea.isNullOrBlank()) {
                    address.subAdminArea
                } else if (!address.adminArea.isNullOrBlank()) {
                    address.adminArea
                } else {
                    address.countryName
                }
            val gpsText = "${address.latitude} ${address.longitude}"
            viewBinding.locationCoordinate.text = gpsText
        }

//        viewBinding.textEdit.setOnFocusChangeListener { view: View, focused: Boolean ->
//            val navigationBar = requireActivity().findViewById<BottomNavigationView>(R.id.navigationView)
//            if (navigationBar == null) {
//               Log.e("WDG_LOG", "no nav bar")
//            } else {
//                navigationBar.visibility =
//                    if (focused) View.INVISIBLE
//                    else View.VISIBLE
//            }
//        }
        viewBinding.textEdit.addTextChangedListener { editable: Editable? ->
            if (editable != null) {
                viewBinding.textCount.text = "${editable.length}/50"
            }
        }
        viewBinding.textCount.text = "0/50"

        return viewBinding.root
    }

}