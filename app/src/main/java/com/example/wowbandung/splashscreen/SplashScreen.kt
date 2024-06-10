package com.example.wowbandung.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wowbandung.AdminMainActivity
import com.example.wowbandung.MainActivity
import com.example.wowbandung.WelcomeActivity
import com.example.wowbandung.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class SplashScreen : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        db = Firebase.database
        Handler().postDelayed({
            checkLogin()
        },2000)
    }

    private fun checkLogin(){
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        val dB = db.getReference("Users")
        dB.child(uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isAdmin = snapshot.child("isAdmin").value
                    if (currentUser == null){
                        startActivity(Intent(this@SplashScreen, WelcomeActivity::class.java))
                        finish()
                    } else if (isAdmin == "1") {
                        startActivity(Intent(this@SplashScreen, AdminMainActivity::class.java))
                        finish()
                    } else{
                        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}