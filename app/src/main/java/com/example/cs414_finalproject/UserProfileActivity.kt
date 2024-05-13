package com.example.cs414_finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileActivity : AppCompatActivity() {

    private val TAG = "UserProfileActivity"
    private lateinit var fireBaseDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        fireBaseDb = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            startRegisterActivity()
        } else {
            findViewById<TextView>(R.id.person_name).text = currentUser.displayName
            findViewById<TextView>(R.id.person_email).text = currentUser.email
        }

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val userEmail = currentUser?.email

        fireBaseDb.collection("favorite_events")
            .whereEqualTo("user", userEmail)
            .orderBy("id")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    Log.d(TAG, "onEvent: -----------------------------")
                    val favoriteEvents = snapshots.toObjects(FavoriteEvent::class.java)
                    showDataInRecyclerView(favoriteEvents)
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }

    private fun showDataInRecyclerView(favoriteEvents: List<FavoriteEvent>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = FavoriteEventsAdapter(favoriteEvents)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun startRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                finish()
                true
            }
            R.id.action_profile -> {
                Toast.makeText(this, "Already at User Profile", Toast.LENGTH_SHORT).show()
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