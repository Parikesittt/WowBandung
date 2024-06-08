package com.example.testtubesmanual

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.testtubesmanual.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var store:FirebaseFirestore
    private lateinit var db:FirebaseDatabase
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        store = Firebase.firestore
        db = Firebase.database
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_button)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val ss = SpannableString("Belum punya akun? Daftar")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {

            override fun onClick(textView: View) {

                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.setColor(Color.BLUE)
                ds.isUnderlineText = true
            }
        }
        ss.setSpan(clickableSpan, 18, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val boldSpan = StyleSpan(Typeface.BOLD)
        ss.setSpan(boldSpan, 18, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        val textView = findViewById<TextView>(R.id.didntHaveAccount)
        textView.text = ss
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT

        binding.buttonLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val pass = binding.passEditText.text.toString()
            validatingEmail(email)
            validatingPassword(pass)
            loginFirebase(email,pass)
        }
    }

    private fun loginFirebase(email: String, pass: String) {
        progressDialog.setMessage("Logging in...")
        progressDialog.show()
        auth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    Toast.makeText(this,"Login berhasil", Toast.LENGTH_SHORT).show()
                    checkUserAccess()
                }else{
                    progressDialog.dismiss()
                    Log.w(RegisterActivity.TAG,"signInWithEmailAndPassword:failure", it.exception)
                }
            }
    }

    private fun checkUserAccess() {
        progressDialog.setMessage("Checking user...")
        val user:FirebaseUser? = auth.currentUser
        val dB = db.getReference("Users")
        dB.child(user?.uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()
                    val isAdmin = snapshot.child("isAdmin").value
                    if (isAdmin == "0"){
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    }
                    else if (isAdmin == "1"){
                        startActivity(Intent(this@LoginActivity,AdminMainActivity::class.java))
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun validatingEmail(email:String){
        if (email.isEmpty()){
            binding.emailEditText.error = "Email harus diisi"
            binding.emailEditText.requestFocus()
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEditText.error = "Email tidak valid"
            binding.emailEditText.requestFocus()
        }
    }

    private fun validatingPassword(pass:String){
        if (pass.isEmpty()){
            binding.passEditText.error = "Password tidak boleh kosong"
            binding.passEditText.requestFocus()
        }
        if (pass.length < 8){
            binding.passEditText.error = "Password kurang dari 8 karakter"
            binding.passEditText.requestFocus()
        }
    }

    private fun updateUI(currentUser:FirebaseUser?){
        if (currentUser!=null){
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }
}