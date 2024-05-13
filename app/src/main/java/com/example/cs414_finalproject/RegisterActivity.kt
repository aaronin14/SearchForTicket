package com.example.cs414_finalproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth


class RegisterActivity : AppCompatActivity() {

    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Get instance of the FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // If currentUser is not null, we have a user and go back to the MainActivity
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val signActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // The user has successfully signed in or he/she is a new user

                    val user = FirebaseAuth.getInstance().currentUser
                    Log.d(TAG, "onActivityResult: $user")

                    //Checking for User (New/Old)
                    if (user?.metadata?.creationTimestamp == user?.metadata?.lastSignInTimestamp) {
                        Toast.makeText(this, "Welcome New User!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Welcome Back!", Toast.LENGTH_SHORT).show()
                    }

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()

                } else {
                    // Sign in failed.
                    val response = IdpResponse.fromResultIntent(result.data)
                    if (response == null) {
                        Log.d(TAG, "onActivityResult: the user has cancelled the sign in request")
                    } else {
                        Log.e(TAG, "onActivityResult: ${response.error?.errorCode}")
                    }
                }
            }

            // Login Button
            findViewById<Button>(R.id.login_button).setOnClickListener {
                val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )

                // Create  sign-in intent
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setTosAndPrivacyPolicyUrls("https://example.com", "https://example.com")
                    .setLogo(R.drawable.baseline_home_24)
                    .setAlwaysShowSignInMethodScreen(true)
                    .setIsSmartLockEnabled(false)
                    .build()

                // Launch sign-in Activity with the sign-in intent above
                signActivityLauncher.launch(signInIntent)
            }
        }
    }


}