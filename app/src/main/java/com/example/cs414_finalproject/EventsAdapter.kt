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
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EventsAdapter(private val events: ArrayList<Event>) : RecyclerView.Adapter<EventsAdapter.MyViewHolder>() {
    private val TAG = "EventsAdapter"
    private lateinit var fireBaseDb: FirebaseFirestore

    inner class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val eventName = itemView.findViewById<TextView>(R.id.tv_event_name)
        val venueName = itemView.findViewById<TextView>(R.id.tv_venue_name)
        val venueLocation = itemView.findViewById<TextView>(R.id.tv_venue_location)
        val eventDateTime = itemView.findViewById<TextView>(R.id.tv_event_date_time)
        val priceRange = itemView.findViewById<TextView>(R.id.tv_price_range)
        val seeTicketButton = itemView.findViewById<Button>(R.id.button_see_tickets)
        val addFavoriteButton = itemView.findViewById<Button>(R.id.button_add_favorite)
        val eventImage = itemView.findViewById<ImageView>(R.id.image_event)

        init{
            fireBaseDb = FirebaseFirestore.getInstance()
            val currentUser = FirebaseAuth.getInstance().currentUser

            seeTicketButton.setOnClickListener {
                val context = itemView.context
                val currentItem = events[position]
                val browserIntent = Intent(Intent.ACTION_VIEW)
                browserIntent.data = Uri.parse(currentItem.url)
                startActivity(context, browserIntent, Bundle())
            }

            addFavoriteButton.setOnClickListener {
                val context = itemView.context
                val currentItem = events[position]
                val highestQualityImage = currentItem.images.maxByOrNull {
                    it.width * it.height
                }
                val highestQualityImageUrl = highestQualityImage?.url

                val events = fireBaseDb.collection("favorite_events")

                val event = hashMapOf(
                    "user" to (currentUser?.email ?: ""),
                    "id" to currentItem.id,
                    "event_name" to eventName.text.toString(),
                    "event_datetime" to eventDateTime.text.toString(),
                    "venue_name" to venueName.text.toString(),
                    "venue_location" to venueLocation.text.toString(),
                    "price_range" to priceRange.text.toString(),
                    "url" to currentItem.url,
                    "image_url" to (highestQualityImageUrl)
                )

                val documentId = events.document().id
                events.document(documentId).set(event)

                val builder = AlertDialog.Builder(context)
                builder.setCancelable(true)
                builder.setTitle("Success")
                builder.setMessage("Favorite Event has been added")
                builder.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = events[position]

        Log.d(TAG, "position=$position")
        Log.d(TAG, "currentItem.name=${currentItem.name}")

        holder.eventName.text = currentItem.name
        holder.venueName.text = currentItem.embedded.venues[0].name

        var dateTime ="Date: "
        if (!currentItem.dates.start.localDate.isNullOrEmpty())
            dateTime += "${currentItem.dates.start.localDate}"
        if (!currentItem.dates.start.localTime.isNullOrEmpty())
            dateTime += " @ ${currentItem.dates.start.localTime}"

        holder.venueLocation.text = "Address: ${currentItem.embedded.venues[0].address.line1}, ${currentItem.embedded.venues[0].city.name}, ${currentItem.embedded.venues[0].state.name}"
        holder.eventDateTime.text = dateTime

        if (currentItem.priceRanges.isNullOrEmpty()) {
            holder.priceRange.isVisible = false
        } else {
            holder.priceRange.text =
                "Price Range: $${currentItem.priceRanges[0].min} - $${currentItem.priceRanges[0].max}"
            holder.priceRange.isVisible = true
        }

        val context = holder.itemView.context
        val highestQualityImage = currentItem.images.maxByOrNull {
            it.width * it.height
        }

        if (highestQualityImage != null) {
            Glide.with(context)
                .load(highestQualityImage.url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.eventImage)
        }
    }


}