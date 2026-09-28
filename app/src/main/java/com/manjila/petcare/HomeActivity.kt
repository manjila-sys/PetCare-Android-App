package com.manjila.petcare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // My Pets
        val myPets = findViewById<Button>(R.id.btnMyPets)

        myPets.setOnClickListener {
            val intent = Intent(this, MyPetsActivity::class.java)
            startActivity(intent)
        }

        // Health
        val health = findViewById<Button>(R.id.btnHealth)

        health.setOnClickListener {
            Toast.makeText(this, "Pet Health section coming soon 🩺", Toast.LENGTH_SHORT).show()
        }

        // Appointments
        val appointments = findViewById<Button>(R.id.btnAppointments)

        appointments.setOnClickListener {
            Toast.makeText(this, "Appointments section coming soon 📅", Toast.LENGTH_SHORT).show()
        }

        // Reminders
        val reminders = findViewById<Button>(R.id.btnReminders)

        reminders.setOnClickListener {
            Toast.makeText(this, "Reminders section coming soon 🔔", Toast.LENGTH_SHORT).show()
        }
    }
}