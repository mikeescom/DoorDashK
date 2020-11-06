package com.mikeescom.doordashk.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.mikeescom.doordashk.R
import com.mikeescom.doordashk.data.models.RestaurantDetail
import com.mikeescom.doordashk.viewmodel.MainViewModel

class DetailFragment : Fragment() {
    private var viewModel: MainViewModel? = null
    private var progressBar: ProgressBar? = null

    private var name: TextView? = null
    private var description: TextView? = null
    private var address: TextView? = null
    private var imageHeader: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        val bundle = arguments
        val id = bundle!!.getLong("ID")
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel!!.init()
        viewModel!!.getRestaurantDetailResponseLiveData(id)
            ?.observe(viewLifecycleOwner) { response ->
                if (response != null) {
                    updateUI(response)
                }
            }
        initViews(view)
        return view
    }

    private fun initViews(view: View) {
        progressBar = view.findViewById(R.id.progressBar)
        imageHeader = view.findViewById(R.id.image_header)
        name = view.findViewById(R.id.name)
        description = view.findViewById(R.id.description)
        address = view.findViewById(R.id.address)
    }

    private fun updateUI(restaurantDetail: RestaurantDetail) {
        if (progressBar != null) {
            progressBar!!.visibility = View.GONE
        }
        Glide.with(requireContext())
            .load(restaurantDetail.cover_img_url)
            .centerCrop()
            .placeholder(R.drawable.placeholder_item)
            .into(imageHeader!!)
        name?.text = restaurantDetail.name
        description?.text = restaurantDetail.description
        address?.text = restaurantDetail.address?.printable_address
    }
}