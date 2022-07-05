package com.example.fasthire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.io.Serializable


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

                        var database = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
                        val userid = firebaseAuth.currentUser!!.uid



                        var intent = Intent(this, ApplicantActivity::class.java)
                        Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                        database.child(userid).addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var user = snapshot.getValue<User>()


                                startActivity(intent.putExtra(
                                    "User",
                                    user
                                ))
                                finish()

                            }

                            override fun onCancelled(error: DatabaseError) {
                            }


                        })


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