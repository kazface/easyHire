package com.example.fasthire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase


class SignInActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth.currentUser
        if(currentUser != null){
//            reload();
        }
        setContentView(R.layout.activity_sign_in)

        var signUpButton = findViewById<Button>(R.id.signUpButton)
        var signInButton = findViewById<Button>(R.id.signInButton)

        var emailInput = findViewById<EditText>(R.id.emailInput)
        var passwordInput = findViewById<EditText>(R.id.passwordInput)
        var progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE

        fun isFormValid(): Boolean {
            if(emailInput.text.isEmpty()){
                return false
            }

            if(passwordInput.text.isEmpty()){
                return false
            }
            return true
        }

        signInButton.setOnClickListener{
            if(isFormValid()){
                progressBar.visibility = View.VISIBLE
                firebaseAuth.signInWithEmailAndPassword(emailInput.text.toString(), passwordInput.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful){
                        val user = firebaseAuth.currentUser

                        Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, it.exception?.localizedMessage ?: "Error!", Toast.LENGTH_SHORT).show()

                    }
                    progressBar.visibility = View.GONE

                }


            }
        }

        signUpButton.setOnClickListener {
            startActivity( Intent(this, SignUpActivity::class.java))
            finish()
        }

    }

    private fun reload() {
        TODO("Not yet implemented")
    }


}