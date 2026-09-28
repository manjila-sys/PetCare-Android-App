package com.manjila.petcare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var database: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        database = UserDatabaseHelper(this)

        val email = findViewById<EditText>(R.id.etLoginEmail)
        val password = findViewById<EditText>(R.id.etLoginPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val createAccount = findViewById<TextView>(R.id.tvCreateAccount)

        loginButton.setOnClickListener {

            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString().trim()

            if (userEmail.isEmpty() || userPassword.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter your email and password",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val validUser = database.checkLogin(
                    userEmail,
                    userPassword
                )

                if (validUser) {

                    Toast.makeText(
                        this,
                        "Login successful!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(
                        this,
                        HomeActivity::class.java
                    )

                    startActivity(intent)
                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Invalid email or password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        createAccount.setOnClickListener {

            val intent = Intent(
                this,
                RegisterActivity::class.java
            )

            startActivity(intent)
        }
    }
}