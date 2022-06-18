package com.example.fasthire

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private var database : FirebaseDatabase = Firebase.database("https://fasthire-ae6c0-default-rtdb.europe-west1.firebasedatabase.app/")


    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_sign_up)


        var signInButton = findViewById<Button>(R.id.signInButton)
        var signUpButton = findViewById<Button>(R.id.signUpButton)


        var emailInput = findViewById<EditText>(R.id.emailInput)

        var fullNameInput = findViewById<EditText>(R.id.fullNameInput)
        var phoneInput = findViewById<EditText>(R.id.phoneInput)

        var passwordInput = findViewById<EditText>(R.id.passwordInput)
        var passwordVerifyInput = findViewById<EditText>(R.id.passwordVerifyInput)
        var progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE

        fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
        fun isPasswordConfirmValid(text: String) = text.toString() == passwordInput.text.toString()

        fun isFormValid(): Boolean {
            if(emailInput.text.isEmpty() && fullNameInput.text.isEmpty() && phoneInput.text.isEmpty()
                && passwordInput.text.isEmpty() && passwordVerifyInput.text.isEmpty()){
                    Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                    return false;
            }
            if(!isPasswordConfirmValid(passwordVerifyInput.text.toString())){
                Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                return false;
            }
            if(!emailInput.text.toString().isValidEmail()){
                Toast.makeText(this, "Please provide valid email", Toast.LENGTH_SHORT).show()
                return false;
            }
            if(phoneInput.text.length < 9 ){
                Toast.makeText(this, "Please provide phone number", Toast.LENGTH_SHORT).show()
                return false;
            }

            return true
        }


        signInButton.setOnClickListener {
            startActivity( Intent(this, SignInActivity::class.java))
            finish()
        }

        emailInput.doOnTextChanged { text, start, before, count ->
            if(text!!.length > 1 && !text.isValidEmail()){
                emailInput.error = "Please enter email"
            }else{
                emailInput.error = null
            }
        }

        passwordVerifyInput.doOnTextChanged { text, start, before, count ->
            if(
                !isPasswordConfirmValid(text.toString())

            ){
                passwordVerifyInput.setError("Please verify  the password", null)
            }else{
                passwordVerifyInput.setError(null, null)
            }

        }

        signUpButton.setOnClickListener {
            if(isFormValid()){

                progressBar.visibility = View.VISIBLE


                firebaseAuth.createUserWithEmailAndPassword(emailInput.text.toString(), passwordInput.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful){

                        var user: User = User(
                            emailInput.text.toString(),
                            "+60 ${phoneInput.text.toString()}",
                            fullNameInput.text.toString(),
                        )
                            database
                                .getReference("Users")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .setValue(user).addOnCompleteListener{ task ->
                                    if(task.isSuccessful){
                                        Toast.makeText(this, "Account registered", Toast.LENGTH_SHORT).show()

                                    }else{
                                        Toast.makeText(this, task.exception?.localizedMessage.toString(), Toast.LENGTH_SHORT).show()
                                    }
                                };




                    }else{
                        Toast.makeText(this, it.exception?.localizedMessage.toString(), Toast.LENGTH_SHORT).show()

                    }
                    progressBar.visibility = View.GONE

                }


            }

        }


    }




}