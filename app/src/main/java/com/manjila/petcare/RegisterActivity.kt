package com.manjila.petcare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var database: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        database = UserDatabaseHelper(this)

        val name = findViewById<EditText>(R.id.etName)
        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val registerButton = findViewById<Button>(R.id.btnRegister)

        registerButton.setOnClickListener {

            val userName = name.text.toString().trim()
            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString().trim()

            if (userName.isEmpty() ||
                userEmail.isEmpty() ||
                userPassword.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(userEmail)
                    .matches()
            ) {

                Toast.makeText(
                    this,
                    "Please enter a valid email address",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (userPassword.length < 6) {

                Toast.makeText(
                    this,
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val registered = database.registerUser(
                    userName,
                    userEmail,
                    userPassword
                )

                if (registered) {

                    Toast.makeText(
                        this,
                        "Account created successfully!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(
                        this,
                        PetProfileActivity::class.java
                    )

                    startActivity(intent)
                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "This email is already registered",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}