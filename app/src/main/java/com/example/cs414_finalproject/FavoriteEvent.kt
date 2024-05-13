package com.example.cs414_finalproject

data class FavoriteEvent (
    var user: String? = null,
    var id: String? = null,
    var event_name: String? = null,
    var event_datetime: String? = null,
    var venue_name: String? = null,
    var venue_location: String? = null,
    var price_range: String? = null,
    var url: String? = null,
    var image_url: String? = null
)