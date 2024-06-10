package com.example.wowbandung

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wowbandung.databinding.ActivityResetPasswordBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding:ActivityResetPasswordBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        binding.buttonReset.setOnClickListener { resetPassword() }
    }

    private fun resetPassword(){
        val emailInputted = binding.emailEditText.text.toString()
        auth.sendPasswordResetEmail(emailInputted)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    AlertDialog.Builder(this@ResetPasswordActivity).apply {
                        setTitle("Done")
                        setMessage("Email berhasil dikirim, cek email untuk reset password anda")
                        setPositiveButton("OK") { _, _ ->
                            finish()
                        }
                        create()
                        show()
                    }
                }
            }
    }
}