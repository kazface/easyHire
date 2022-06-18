package com.example.fasthire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.fasthire.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var signUpButton = findViewById<Button>(R.id.signUpButton)

        signUpButton.setOnClickListener {
            startActivity( Intent(this, SignUpActivity::class.java))
            finish()
        }

    }



}