package com.example.cs414_finalproject

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TicketService {

    // https://app.ticketmaster.com/discovery/v2/events.json?apikey=Gbosc8tNNwkUG60CtVQhy24p0WScy6zB&size=5&keyword=music&city=Hartford&sort=name,desc
    @GET("events.json")
    fun getEventsInfo(@Query("apikey") apikey: String,
                      @Query("size") size: Int,
                      @Query("keyword") keyword: String,
                      @Query("city") city: String,
                      @Query("sort") sort: String) : Call<EventInfo>

}