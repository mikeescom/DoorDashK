package com.mikeescom.doordashk.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikeescom.doordashk.R
import com.mikeescom.doordashk.data.models.Restaurant
import java.lang.String

class ListAdapter(private val context: Context) : RecyclerView.Adapter<ListAdapter.ListAdapterHolder>() {
    private var results: Array<Restaurant>? = null
    private var listener: OnClickItemListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapterHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        return ListAdapterHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListAdapterHolder, position: Int) {
        val restaurant: Restaurant = results!![position]
        holder.itemView.setOnClickListener { listener!!.onClickItem(restaurant.id) }
        Glide.with(context)
                .load(restaurant.cover_img_url)
                .centerCrop()
                .placeholder(R.drawable.placeholder_item)
                .into(holder.foodImage)
        holder.deliveryFee.text = String.valueOf(restaurant.delivery_fee)
        holder.name.text = restaurant.name
        holder.asapTime.text = String.valueOf(restaurant.asap_time)
        holder.rating.text = String.valueOf(restaurant.average_rating)
        holder.ratingNumbers.text = String.valueOf(restaurant.number_of_ratings)
    }

    override fun getItemCount(): Int {
        return if (results == null) {
            0
        } else results!!.size
    }

    fun setResults(results: Array<Restaurant>?) {
        this.results = results
        notifyDataSetChanged()
    }

    fun setOnClickItemListener(listener: OnClickItemListener) {
        this.listener = listener
    }

    inner class ListAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodImage: ImageView = itemView.findViewById(R.id.food_image)
        val deliveryFee: TextView = itemView.findViewById(R.id.delivery_fee)
        val name: TextView = itemView.findViewById(R.id.name)
        val asapTime: TextView = itemView.findViewById(R.id.asap_time)
        val rating: TextView = itemView.findViewById(R.id.rating)
        val ratingNumbers: TextView = itemView.findViewById(R.id.ratings_number)
    }

    interface OnClickItemListener {
        fun onClickItem(id: Long)
    }
}