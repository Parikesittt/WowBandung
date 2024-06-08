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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.testtubesmanual.databinding.ActivityRegisterBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import com.google.firebase.auth.FacebookAuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding:ActivityRegisterBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var store: FirebaseFirestore
    private lateinit var db:FirebaseDatabase
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
        val ss = SpannableString("Sudah punya akun? Masuk")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {

            override fun onClick(textView: View) {

                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
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


        val textView = binding.haveAccount
        textView.text = ss
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
        binding.google.setOnClickListener{
            signIn()
        }
        binding.buttonSignUp.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val pass = binding.passEditText.text.toString()
            validatingEmail(email)
            validatingPassword(pass)
            registerFirebase(email,pass)
        }
    }

    private fun registerFirebase(email: String,pass: String){
        progressDialog.setMessage("Creating account...")
        progressDialog.show()
        auth.createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    Toast.makeText(this,"Registrasi berhasil", Toast.LENGTH_SHORT).show()
                    updateUserInfo()
                }else{
                    Log.w(TAG,"signInWithEmailAndPassword:failure", it.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUserInfo(){
        progressDialog.setMessage("Saving user info...")
        val user:FirebaseUser? = auth.currentUser
        val name = binding.nameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val uid = auth.currentUser?.uid
        val ref = db.getReference("Users")
        val userData = hashMapOf(
            "nama" to name,
            "email" to email,
            "profileImage" to "",
            "isAdmin" to "0"
        )
        ref.child(uid.toString())
            .setValue(userData)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Account created...",Toast.LENGTH_SHORT).show()
                updateUI(user)
            }
            .addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to save user info due to ${e.message}",Toast.LENGTH_SHORT).show()
            }

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

    private fun signIn() {
        val credentialManager = CredentialManager.create(this)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.web_client_id))
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result:GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = this@RegisterActivity
                )
                handleSignIn(result)
            }catch (e:GetCredentialException){
                Log.d("Error", e.message.toString())
            }
        }
    }

    private fun handleSignIn(result:GetCredentialResponse){
        when(val credential = result.credential){
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    }catch (e:GoogleIdTokenParsingException){
                        Log.e(TAG,"Received an invalid google id token response", e)
                    }
                }else{
                    Log.e(TAG,"Unexpected type of credential")
                }
            }
            else -> {
                Log.e(TAG,"Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken:String){
        val credential:AuthCredential = GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful){
                    Log.d(TAG,"signInWithCredential:success")
                    val user:FirebaseUser? = auth.currentUser
                    updateUI(user)
                }else{
                    Log.w(TAG,"signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser:FirebaseUser?){
        if (currentUser!=null){
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }
    }

    companion object{
        const val TAG = "LoginActivity"
    }
}