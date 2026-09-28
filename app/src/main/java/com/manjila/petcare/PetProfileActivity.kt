package com.manjila.petcare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PetProfileActivity : AppCompatActivity() {

    private lateinit var database: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_profile)

        database = UserDatabaseHelper(this)

        val petName = findViewById<EditText>(R.id.etPetName)
        val petType = findViewById<EditText>(R.id.etPetType)
        val petAge = findViewById<EditText>(R.id.etPetAge)
        val continueButton = findViewById<Button>(R.id.btnContinue)

        // Check whether we are editing an existing pet
        val petId = intent.getIntExtra("pet_id", -1)

        // If editing, load existing pet information
        if (petId != -1) {

            val petNameValue = intent.getStringExtra("pet_name")
            val petTypeValue = intent.getStringExtra("pet_type")
            val petAgeValue = intent.getIntExtra("pet_age", 0)

            petName.setText(petNameValue)
            petType.setText(petTypeValue)
            petAge.setText(petAgeValue.toString())

            continueButton.text = "Update Pet"
        }

        continueButton.setOnClickListener {

            val name = petName.text.toString().trim()
            val type = petType.text.toString().trim()
            val ageText = petAge.text.toString().trim()

            if (name.isEmpty() || type.isEmpty() || ageText.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please fill in all pet information",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val age = ageText.toIntOrNull()

            if (age == null) {

                Toast.makeText(
                    this,
                    "Please enter a valid age",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (petId != -1) {

                // UPDATE existing pet
                val updated = database.updatePet(
                    petId,
                    name,
                    type,
                    age
                )

                if (updated) {

                    Toast.makeText(
                        this,
                        "$name's profile updated successfully! 🐾",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Failed to update pet profile",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {

                // ADD new pet
                val ownerEmail = "current_user"

                val saved = database.addPet(
                    ownerEmail,
                    name,
                    type,
                    age
                )

                if (saved) {

                    Toast.makeText(
                        this,
                        "$name's profile saved successfully! 🐾",
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
                        "Failed to save pet profile",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}