package com.example.cs414_finalproject

import com.google.gson.annotations.SerializedName

data class EventInfo (
    @SerializedName("_embedded") val embedded: Events
)

data class Events (
    val events: List<Event>
)

data class Event (
    val name: String,
    val id: String,
    val url: String,
    val images: List<Pictures>,
    val dates: Dates,
    val priceRanges: List<PriceRange>,
    @SerializedName("_embedded") val embedded: Embedded
)

data class Pictures (
    val ratio: String,
    val url: String,
    val width: Int,
    val height: Int
)

data class Dates(
    val start: StartDates
)

data class StartDates(
    val localDate: String,
    val localTime: String
)

data class PriceRange (
    val currency: String,
    val min: Float,
    val max: Float
)

data class Embedded (
    val venues: List<Venue>
)

data class Venue (
    val name: String,
    val city: City,
    val state: State,
    val address: Address
)

data class City (
    val name: String
)

data class State (
    val name: String
)

data class Address (
    val line1: String,
    val line2: String
)