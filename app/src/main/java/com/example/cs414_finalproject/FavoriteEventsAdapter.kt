package com.example.cs414_finalproject

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class FavoriteEventsAdapter(private val favoriteEvents: List<FavoriteEvent>) : RecyclerView.Adapter<FavoriteEventsAdapter.MyViewHolder>() {

    private val TAG = "FavoriteEventsAdapter"
    private lateinit var fireBaseDb: FirebaseFirestore

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val eventName = itemView.findViewById<TextView>(R.id.tv_event_name)
        val venueName = itemView.findViewById<TextView>(R.id.tv_venue_name)
        val venueLocation = itemView.findViewById<TextView>(R.id.tv_venue_location)
        val eventDateTime = itemView.findViewById<TextView>(R.id.tv_event_date_time)
        val priceRange = itemView.findViewById<TextView>(R.id.tv_price_range)
        val seeTicketButton = itemView.findViewById<Button>(R.id.button_see_tickets)
        val removeFavoriteButton = itemView.findViewById<Button>(R.id.button_remove_favorite)
        val eventImage = itemView.findViewById<ImageView>(R.id.image_event)

        init{
            fireBaseDb = FirebaseFirestore.getInstance()

            seeTicketButton.setOnClickListener {
                val context = itemView.context
                val currentItem = favoriteEvents[position]
                val browserIntent = Intent(Intent.ACTION_VIEW)
                browserIntent.data = Uri.parse(currentItem.url)
                ContextCompat.startActivity(context, browserIntent, Bundle())
            }

            removeFavoriteButton.setOnClickListener {
                val context = itemView.context
                val currentItem = favoriteEvents[position]

                val id = currentItem.id

                fireBaseDb.collection("favorite_events")
                    .whereEqualTo("id", id)
                    .get()
                    .addOnSuccessListener { documents->

                        for (document in documents) {
                            if (document != null) {
                                Log.d(TAG, "${document.id} => ${document.data}")
                                document.reference.delete()

                                val builder = AlertDialog.Builder(context)
                                builder.setCancelable(true)
                                builder.setTitle("Success")
                                builder.setMessage("Favorite Event has been removed")
                                builder.show()
                                break
                            } else {
                                Log.d(TAG, "No such document")
                            }
                        }
                    }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_row_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return favoriteEvents.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = favoriteEvents[position]

        holder.eventName.text = currentItem.event_name
        holder.venueName.text = currentItem.venue_name
        holder.venueLocation.text = currentItem.venue_location
        holder.eventDateTime.text = currentItem.event_datetime
        holder.priceRange.text = currentItem.price_range

        val context = holder.itemView.context

        if(currentItem.image_url!=null) {
            Glide.with(context)
                .load(currentItem.image_url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.eventImage)
        }
    }
}