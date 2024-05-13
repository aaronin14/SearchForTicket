package com.example.cs414_finalproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val BASE_URL = "https://app.ticketmaster.com/discovery/v2/"
    private val apikey = "Gbosc8tNNwkUG60CtVQhy24p0WScy6zB"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // #### Authentication using FirebaseAuth #####

        // Get instance of the FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // If currentUser is null, open the RegisterActivity
        if (currentUser == null) {
            startRegisterActivity()
        }

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    fun searchButton(view: View) {
        val keyword = findViewById<EditText>(R.id.keyword_input).text.toString()
        val location = findViewById<EditText>(R.id.location_input).text.toString()
        view.hideKeyboard()

        if (keyword.isEmpty() && location.isEmpty())
            buildDialog("Search Term and City")
        else if (keyword.isEmpty())
            buildDialog("Search Term")
        else if (location.isEmpty())
            buildDialog("City")
        else {
            val eventsList = ArrayList<Event>()
            val adapter = EventsAdapter(eventsList)
            val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
            recyclerView.adapter = adapter
            recyclerView.layoutManager= LinearLayoutManager(this)

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val eventsAPI = retrofit.create(TicketService::class.java)

            eventsAPI.getEventsInfo(apikey, 20, keyword, location, "date,asc").enqueue(object:
                Callback<EventInfo> {
                override fun onResponse(call: Call<EventInfo>, response: Response<EventInfo>) {
                    Log.d(TAG, "onResponse: $response")
                    val body = response.body()
                    if (body == null) {
                        Log.w(TAG, "Valid response was not received")
                        return
                    }
                    if (body.embedded != null) {
                        findViewById<TextView>(R.id.tv_no_results).isVisible = false
                        eventsList.addAll(body.embedded.events)
                        Log.d(TAG, "eventsList: ${eventsList.toString()}")
                        adapter.notifyDataSetChanged()
                    } else {
                        findViewById<TextView>(R.id.tv_no_results).isVisible = true
                    }
                }

                override fun onFailure(call: Call<EventInfo>, t: Throwable) {
                    Log.d(TAG, "onFailure : $t")
                }


            })
        }
    }

    private fun buildDialog(missingText: String) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setIcon(android.R.drawable.ic_delete)
        builder.setTitle("$missingText missing")
        builder.setMessage("$missingText cannot be empty. Please enter a $missingText")
        builder.setPositiveButton("OKAY") { dialog, which ->
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun View.hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun startRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startUserProfileActivity() {
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
    }


    // This override function is used to create menu option where you can see on the top right corner
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_options, menu)
        return true
    }

    // This override function is used to handle if menu_option (logout) is selected.
    // If so, the user will be signed out.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                Toast.makeText(this, "Already at Homepage", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_profile -> {
                // User chose the "profile" item,
                Toast.makeText(this, "User Profile", Toast.LENGTH_SHORT).show()
                startUserProfileActivity()
                true
            }
            R.id.action_logout -> {
                // User chose the "logout" item, logout the user then
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()

                AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // After logout, start the RegisterActivity again
                            finish()
                            startRegisterActivity()
                        } else {
                            Log.e(TAG, "Task is not successful:${task.exception}")
                        }
                    }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}